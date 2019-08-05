package com.bubllbub.exchangerates.models.retrofit.apiDatas

import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.models.Repository
import com.bubllbub.exchangerates.models.retrofit.APIService
import com.bubllbub.exchangerates.models.retrofit.NbrbApiData
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Rate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class RateApiData : DataSource<Rate> {
    private val jSONApi = APIService.instance.getJSONApi()

    override fun getAll(): Flowable<List<Rate>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAll(query: DataSource.Query<Rate>): Flowable<List<Rate>> {

        return when {
            (query.has(Repository.CUR_ID) && query.has(Repository.START_DATE) && query.has(
                Repository.END_DATE
            )) -> {
                val dateStartString = query.get(Repository.START_DATE)!!
                val dateEndString = query.get(Repository.END_DATE)!!
                val curIdQuery = query.get(Repository.CUR_ID)?.toInt()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateStart = dateFormat.parse(dateStartString)
                val dateEnd = dateFormat.parse(dateEndString)
                var existCurrencyFromList: Currency? = null
                val apiCurrency =
                    NbrbApiData.of<Currency>(Currency::class)

                val allCurrencies = arrayListOf<Currency>()

                return apiCurrency.getAll()
                    .flatMap {
                        allCurrencies.addAll(it)
                        existCurrencyFromList =
                            it.find { currency -> currency.curId == curIdQuery && currency.curDateStart <= dateStart && currency.curDateEnd >= dateEnd }
                        jSONApi.getRatesOnDate(
                            dateStartString,
                            existCurrencyFromList!!.curPeriodicity
                        )
                    }.flatMap {
                        val needRate =
                            it.find { currency -> currency.curAbbreviation == existCurrencyFromList!!.curAbbreviation }
                        val needCurrency = allCurrencies.find { it.curId == needRate!!.curId }

                        if (needCurrency!!.curDateEnd >= dateEnd) {
                            jSONApi.getDynamicsRate(
                                needCurrency.curId,
                                dateStartString,
                                dateEndString
                            )
                                .flatMap { list ->
                                    list.forEach { rate ->
                                        rate.scale = needCurrency.scale
                                        rate.curAbbreviation = needCurrency.curAbbreviation
                                        rate.rateId =
                                            needCurrency.curAbbreviation + "_" + DateTimeFormat.forPattern("yyyy-MM-dd").print(
                                                rate.date
                                            )
                                    }
                                    Flowable.just(list)
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
                            startRates.zipWith(
                                endRates,
                                BiFunction { startRatesList, endRatesList ->
                                    startRatesList.map { startRateCurrency ->
                                        startRateCurrency.scale = needCurrency.scale
                                        startRateCurrency.rateId =
                                            needCurrency.curAbbreviation + "_" + DateTimeFormat.forPattern("yyyy-MM-dd").print(
                                                startRateCurrency.date
                                            )
                                        startRateCurrency.curAbbreviation =
                                            needCurrency.curAbbreviation
                                    }
                                    endRatesList.map { endRateCurrency ->
                                        endRateCurrency.scale = nextCurrency.scale
                                        endRateCurrency.rateId =
                                            nextCurrency.curAbbreviation + "_" + DateTimeFormat.forPattern("yyyy-MM-dd").print(
                                                endRateCurrency.date
                                            )
                                        endRateCurrency.curAbbreviation =
                                            nextCurrency.curAbbreviation
                                    }
                                    val maxRates = max(needCurrency.scale, nextCurrency.scale)
                                    startRatesList.addAll(endRatesList)
                                    startRatesList
                                        .filter { currency -> currency.scale < maxRates }
                                        .map { currency ->
                                            currency.curOfficialRate =
                                                currency.curOfficialRate / maxRates / currency.scale
                                        }
                                    startRatesList
                                })
                        }
                    }
            }
            else -> {
                Flowable.just(listOf())
            }
        }
    }

    override fun get(query: DataSource.Query<Rate>): Observable<Rate> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveAll(list: List<Rate>): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(item: Rate): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(item: Rate): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}