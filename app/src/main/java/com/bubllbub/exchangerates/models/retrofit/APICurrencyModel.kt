package com.bubllbub.exchangerates.models.retrofit

import com.bubllbub.exchangerates.enums.CurrencyRes
import com.bubllbub.exchangerates.models.room.AppDatabase
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.CurrencyFavorite
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

private val BYN = Currency(
    curOfficialRate = 1.0,
    curAbbreviation = "BYN",
    curName = "Белорусский рубль",
    curNameBel = "Беларускі рубель",
    curNameEng = "Belarusian ruble",
    symbol = CurrencyRes.valueOf("BYN").getSymbolRes()
)

class APICurrencyModel {
    private val startingCurrenciesAbbreviations = listOf("USD", "EUR", "RUB")

    private fun getCurrenciesList(
        ids: ArrayList<Int>,
        appDatabase: AppDatabase
    ): Observable<List<Currency>> {
        val requests = arrayListOf<Observable<Currency>>()

        val jSONApi = APIService.instance.getJSONApi()

        ids.forEach {

            val currencyApi = jSONApi.getCurrencyWithID(it)
            val ratesAPI = jSONApi.getRatesWithID(it)
            requests.add(currencyApi.zipWith(ratesAPI, BiFunction { currency, rate ->
                currency.date = rate.date
                currency.curOfficialRate = rate.curOfficialRate
                currency.symbol = CurrencyRes.valueOf(currency.curAbbreviation).getSymbolRes()
                currency
            }))
        }

        return Observable.zip(requests) { list ->
            val listForInsert: List<CurrencyFavorite> =
                list.map { CurrencyFavorite(currency = it as Currency) }
            appDatabase.currencyFavoritesDao().insertAll(listForInsert)
            list.map { obj -> obj as Currency }
        } as Observable<List<Currency>>
    }

    fun getDynamicsRates(
        name: String = "USD",
        dateStart: Date,
        dateEnd: Date
    ): Observable<ArrayList<Currency>> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateStartString = dateFormat.format(dateStart)
        val dateEndString = dateFormat.format(dateEnd)
        val jSONApi = APIService.instance.getJSONApi()
        var existCurrencyFromList: Currency? = null

        val allCurrencies = arrayListOf<Currency>()

        return jSONApi.getCurrencies()
            .flatMap {
                allCurrencies.addAll(it)
                existCurrencyFromList =
                    it.find { currency -> currency.curName == name && currency.curDateStart <= dateStart && currency.curDateEnd >= dateEnd }
                jSONApi.getRatesOnDate(dateStartString, existCurrencyFromList!!.curPeriodicity)
            }.flatMap {
                val needRate =
                    it.find { currency -> currency.curAbbreviation == existCurrencyFromList!!.curAbbreviation }
                val needCurrency = allCurrencies.find { it.curId == needRate!!.curId }

                if (needCurrency!!.curDateEnd >= dateEnd) {
                    jSONApi.getDynamicsRate(needCurrency.curId, dateStartString, dateEndString)
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
                        startRatesList
                    })
                }
            }
    }

    fun getStartingCurrencies(appDatabase: AppDatabase): Observable<List<Currency>> {
        val jSONApi = APIService.instance.getJSONApi()
        return jSONApi.getCurrencies()
            .flatMap { list ->
                val actual = list.filter { curr ->
                    curr.curDateStart <= Date() && curr.curDateEnd >= Date() && startingCurrenciesAbbreviations.contains(
                        curr.curAbbreviation
                    )
                }
                getCurrenciesList(ArrayList(actual.map { it.curId }), appDatabase)
            }
    }

    fun getConverterStartingCurrencies(): Observable<List<Currency>> {
        val jSONApi = APIService.instance.getJSONApi()
        return jSONApi.getCurrencies()
            .flatMap { list ->
                val actual = list.filter { curr ->
                    curr.curDateStart <= Date() && curr.curDateEnd >= Date() && startingCurrenciesAbbreviations.contains(
                        curr.curAbbreviation
                    )
                }

                val requests = arrayListOf<Observable<Currency>>()

                ArrayList(actual.map { it.curId }).forEach {

                    val currencyApi = jSONApi.getCurrencyWithID(it)
                    val ratesAPI = jSONApi.getRatesWithID(it)
                    requests.add(currencyApi.zipWith(ratesAPI, BiFunction { currency, rate ->
                        currency.date = rate.date
                        currency.curOfficialRate = rate.curOfficialRate
                        currency.symbol =
                            CurrencyRes.valueOf(currency.curAbbreviation).getSymbolRes()
                        currency
                    }))
                }

                Observable.zip(requests) { listWithRates ->
                    val fullList = listWithRates.map { obj -> obj as Currency }.toMutableList()
                    fullList.add(BYN)
                    fullList.sortedBy { CurrencyRes.valueOf((it as Currency).curAbbreviation).ordinal }
                } as Observable<List<Currency>>
            }
    }

    fun getActualCurrencies(): Observable<List<Currency>> {
        val jSONApi = APIService.instance.getJSONApi()
        return jSONApi.getCurrencies()
            .flatMap {
                val actual =
                    it.filter { curr -> curr.curDateStart <= Date() && curr.curDateEnd >= Date() }
                Observable.just(actual)
            }
    }

    fun getActualCurrenciesForDialog(appDatabase: AppDatabase): Observable<MutableList<Currency>> {
        val jSONApi = APIService.instance.getJSONApi()
        return jSONApi.getCurrencies()
            .flatMap { list ->
                val actual =
                    list.filter { curr -> curr.curDateStart <= Date() && curr.curDateEnd >= Date() }
                actual.map { curr ->
                    curr.symbol = CurrencyRes.valueOf(curr.curAbbreviation).getSymbolRes()
                }
                val actualSorted =
                    actual.sortedBy { CurrencyRes.valueOf(it.curAbbreviation).ordinal }

                val favsCurr = appDatabase.currencyFavoritesDao().getAll()
                    .toObservable()
                    .flatMap { listFavCurr ->
                        val curr = listFavCurr.map { it.currency }
                        Observable.just(curr)
                    }

                favsCurr.zipWith(
                    Observable.just(actualSorted),
                    BiFunction { favCurrList: List<Currency>, allCurr: List<Currency> ->
                        val filter =
                            allCurr.filterNot { favCurrList.find { favCur -> favCur.curId == it.curId } != null }
                        filter.toMutableList()
                    }) as Observable<MutableList<Currency>>
            }
    }

    fun insertCurrency(
        currency: Currency,
        appDatabase: AppDatabase
    ): Completable {
        val jSONApi = APIService.instance.getJSONApi()

        return Completable.fromObservable(
            jSONApi.getRatesWithID(currency.curId)
                .flatMap {
                    currency.date = it.date
                    currency.curOfficialRate = it.curOfficialRate
                    currency.symbol = CurrencyRes.valueOf(currency.curAbbreviation).getSymbolRes()
                    Observable.just(currency)
                }
                .flatMap {
                    appDatabase.currencyFavoritesDao().insert(CurrencyFavorite(currency = it))
                        .toObservable<Currency>()
                }
        )
    }

    fun deleteFavCurrency(
        currency: CurrencyFavorite,
        appDatabase: AppDatabase
    ): Completable {
        return appDatabase.currencyFavoritesDao().delete(currency.currency.curId)
    }

    fun insertFavCurrency(
        currency: CurrencyFavorite,
        appDatabase: AppDatabase
    ): Completable {
        return appDatabase.currencyFavoritesDao().insert(currency)
    }

    fun getRatesWithNameOnDate(
        name: String = "USD",
        date: Date
    ): Observable<Currency> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(date)
        val jSONApi = APIService.instance.getJSONApi()

        val allCurrencies = jSONApi.getCurrencies()
        val rate = jSONApi.getRatesOnDateWithName(name, dateString)


        return rate.zipWith(allCurrencies, BiFunction { rateCurr, currencies ->
            val currency = currencies.find { it.curId == rateCurr.curId }
            currency?.let {
                it.date = rateCurr.date
                it.curOfficialRate = rateCurr.curOfficialRate
                it.symbol = CurrencyRes.valueOf(it.curAbbreviation).getSymbolRes()
            }
            currency ?: rateCurr
        })
    }
}