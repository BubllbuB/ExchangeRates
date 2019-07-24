package com.bubllbub.exchangerates.models.retrofit

import com.bubllbub.exchangerates.enums.CurrencyRes
import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.models.Repository
import com.bubllbub.exchangerates.objects.Currency
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class CurrencyApiData : DataSource<Currency> {
    private val jSONApi = APIService.instance.getJSONApi()
    private val startingCurrenciesAbbreviations = listOf("USD", "EUR", "RUB")

    override fun getAll(): Flowable<List<Currency>> {
        val requests = arrayListOf<Observable<Currency>>()

        return jSONApi.getCurrencies()
            .flatMap { list ->
                val actual = list.filter { curr ->
                    curr.curDateStart <= Date() && curr.curDateEnd >= Date() && startingCurrenciesAbbreviations.contains(
                        curr.curAbbreviation
                    )
                }
                Observable.just(actual)
            }
            .flatMap { list ->
                list.forEach { curr ->
                    val ratesAPI = jSONApi.getRatesWithID(curr.curId)
                        .flatMap { rate ->
                            curr.date = rate.date
                            curr.curOfficialRate = rate.curOfficialRate
                            curr.symbol = CurrencyRes.valueOf(curr.curAbbreviation).getSymbolRes()
                            Observable.just(curr)
                        }
                    requests.add(ratesAPI)
                }
                Observable.zip(requests) {
                    it.map { obj -> obj as Currency }
                } as Observable<List<Currency>>
            }
            .toFlowable(BackpressureStrategy.LATEST)
    }

    override fun getAll(query: DataSource.Query<Currency>): Flowable<List<Currency>> {
        return when {
            ( query.has(Repository.CUR_NAME) && query.has(Repository.START_DATE) && query.has(Repository.END_DATE) ) -> {
                val curName = query.get(Repository.CUR_NAME) as String
                val startDate =  query.get(Repository.START_DATE) as Date
                val endDate =  query.get(Repository.START_DATE) as Date

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateStartString = dateFormat.format(startDate)
                val dateEndString = dateFormat.format(endDate)
                var existCurrencyFromList: Currency? = null

                val allCurrencies = arrayListOf<Currency>()

                jSONApi.getCurrencies()
                    .flatMap {
                        allCurrencies.addAll(it)
                        existCurrencyFromList =
                            it.find { currency -> currency.curName == curName && currency.curDateStart <= startDate && currency.curDateEnd >= endDate }
                        jSONApi.getRatesOnDate(dateStartString, existCurrencyFromList!!.curPeriodicity)
                    }.flatMap { list ->
                        val needRate =
                            list.find { currency -> currency.curAbbreviation == existCurrencyFromList!!.curAbbreviation }
                        val needCurrency = allCurrencies.find { it.curId == needRate!!.curId }

                        if (needCurrency!!.curDateEnd >= endDate) {
                            jSONApi.getDynamicsRate(needCurrency.curId, dateStartString, dateEndString)
                                .flatMap {
                                    Observable.just(it.toList())
                                }
                        } else {
                            val nextCurrency =
                                allCurrencies.find { nextCurr -> nextCurr.parentId == needCurrency.curId && nextCurr.curDateStart >= needCurrency.curDateEnd }
                            val startRates = jSONApi.getDynamicsRate(
                                needCurrency.curId,
                                dateStartString,
                                dateFormat.format(needCurrency.curDateEnd)
                            )
                            val endRates = jSONApi.getDynamicsRate(
                                nextCurrency!!.curId,
                                dateFormat.format(nextCurrency.curDateStart),
                                dateEndString
                            )
                            startRates.zipWith(endRates, BiFunction { startRatesList, endRatesList ->
                                startRatesList.map { startRateCurrency ->
                                    startRateCurrency.scale = needCurrency.scale
                                }
                                endRatesList.map { endRateCurrency ->
                                    endRateCurrency.scale = nextCurrency.scale
                                }
                                val maxRates = max(needCurrency.scale, nextCurrency.scale)
                                startRatesList.addAll(endRatesList)
                                startRatesList
                                    .filter { currency -> currency.scale < maxRates }
                                    .map { currency ->
                                        currency.curOfficialRate =
                                            currency.curOfficialRate / maxRates / currency.scale
                                    }
                                startRatesList.toList()
                            })
                        }
                    }
                    .toFlowable(BackpressureStrategy.LATEST)

            }
            else -> throw IllegalArgumentException("Unsupported query $query for Currency")
        }
    }

    override fun get(query: DataSource.Query<Currency>): Observable<Currency> {
        return when {
            ( query.has(Repository.CUR_NAME) && query.has(Repository.ON_DATE) ) -> {
                val curAbbreviation = query.get(Repository.CUR_NAME) as String
                val curDate =  query.get(Repository.ON_DATE) as Date

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateString = dateFormat.format(curDate)

                jSONApi.getRatesOnDateWithName(curAbbreviation, dateString)
                    .flatMap {rateCurr ->
                        jSONApi.getCurrencyWithID(rateCurr.curId)
                            .flatMap {
                                it.date = rateCurr.date
                                it.curOfficialRate = rateCurr.curOfficialRate
                                it.symbol = CurrencyRes.valueOf(it.curAbbreviation).getSymbolRes()
                                Observable.just(it)
                                }
                    }

            }
            else -> throw IllegalArgumentException("Unsupported query $query for Currency")
        }
    }

    override fun delete(item: Currency): Completable {
        TODO("not implemented")
    }

    override fun save(item: Currency): Completable {
        TODO("not implemented")
    }

    override fun saveAll(list: List<Currency>): Completable {
        TODO("not implemented")
    }
}
