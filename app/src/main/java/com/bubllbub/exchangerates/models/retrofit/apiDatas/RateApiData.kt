package com.bubllbub.exchangerates.models.retrofit.apiDatas

import com.bubllbub.exchangerates.models.CUR_ID
import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.models.END_DATE
import com.bubllbub.exchangerates.models.START_DATE
import com.bubllbub.exchangerates.models.retrofit.JSONNbrbAPI
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Rate
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.max

class RateApiData @Inject constructor(private val jSONApi: JSONNbrbAPI) : DataSource<Rate> {
    @Inject
    lateinit var apiCurrency: CurrencyApiData

    override fun getAll(): Flowable<List<Rate>> {
        val allCurrencies = arrayListOf<Currency>()

        return apiCurrency.getAll()
            .flatMap {
                allCurrencies.addAll(it)

                if (LocalDate() == LocalDate().withDayOfMonth(1)) {
                    val dailyRates = jSONApi.getActualRatesDaily()
                    val monthlyRates = jSONApi.getActualRatesMonthly()

                    dailyRates.zipWith(
                        monthlyRates,
                        BiFunction { listDaily: ArrayList<Rate>, listMonthly: ArrayList<Rate> ->
                            listDaily.addAll(listMonthly)
                            listDaily
                        })
                } else {
                    jSONApi.getActualRatesDaily()
                }
            }.flatMap {
                it.forEach { rate ->
                    allCurrencies.find { curr -> curr.curId == rate.curId }?.let { needCurrency ->
                        rate.scale = needCurrency.scale
                        rate.curAbbreviation = needCurrency.curAbbreviation
                        rate.rateId =
                            needCurrency.curAbbreviation + "_" + DateTimeFormat.forPattern("yyyy-MM-dd").print(
                                rate.date
                            )
                    }
                }
                Flowable.just(it)
            }
    }

    override fun getAll(query: DataSource.Query<Rate>): Flowable<List<Rate>> {

        return when {
            (query.has(CUR_ID) && query.has(START_DATE) && query.has(
                END_DATE
            )) -> {
                val dateStartString = query.get(START_DATE)!!
                val dateEndString = query.get(END_DATE)!!
                val curIdQuery = query.get(CUR_ID)?.toInt()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateStart = dateFormat.parse(dateStartString)
                val dateEnd = dateFormat.parse(dateEndString)
                var existCurrencyFromList: Currency? = null

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
                        val needCurrency =
                            allCurrencies.find { allCurr -> allCurr.curId == needRate!!.curId }

                        if (needCurrency!!.curDateEnd >= dateEnd) {

                            if (needCurrency.curPeriodicity == 0) {
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
                                                needCurrency.curAbbreviation + "_" + DateTimeFormat.forPattern(
                                                    "yyyy-MM-dd"
                                                ).print(
                                                    rate.date
                                                )
                                        }
                                        Flowable.just(list)
                                    }
                            } else {
                                val dateFormatDateTime = DateTimeFormat.forPattern("yyyy-MM-dd")

                                var startMonth =
                                    dateFormatDateTime.parseDateTime(dateStartString)
                                        .withDayOfMonth(1)
                                val finishMonth =
                                    dateFormatDateTime.parseDateTime(dateEndString)
                                        .withDayOfMonth(1)

                                val requests = mutableListOf<Observable<Rate>>()

                                while (startMonth <= finishMonth) {
                                    requests.add(
                                        jSONApi.getRatesOnDateWithId(
                                            needCurrency.curId,
                                            dateFormatDateTime.print(startMonth)
                                        )
                                    )
                                    startMonth = startMonth.plusMonths(1)
                                }

                                Observable.zip(requests) { ratesList ->
                                    val list = ratesList.map { obj -> obj as Rate }
                                    list.forEach { rate ->
                                        rate.scale = needCurrency.scale
                                        rate.curAbbreviation = needCurrency.curAbbreviation
                                        rate.rateId =
                                            needCurrency.curAbbreviation + "_" + DateTimeFormat.forPattern(
                                                "yyyy-MM-dd"
                                            ).print(
                                                rate.date
                                            )
                                    }
                                    list
                                }.toFlowable(BackpressureStrategy.LATEST)
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
                                            needCurrency.curAbbreviation + "_" + DateTimeFormat.forPattern(
                                                "yyyy-MM-dd"
                                            ).print(
                                                startRateCurrency.date
                                            )
                                        startRateCurrency.curAbbreviation =
                                            needCurrency.curAbbreviation
                                    }
                                    endRatesList.map { endRateCurrency ->
                                        endRateCurrency.scale = nextCurrency.scale
                                        endRateCurrency.rateId =
                                            nextCurrency.curAbbreviation + "_" + DateTimeFormat.forPattern(
                                                "yyyy-MM-dd"
                                            ).print(
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