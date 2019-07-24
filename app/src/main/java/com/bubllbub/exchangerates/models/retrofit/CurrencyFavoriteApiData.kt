package com.bubllbub.exchangerates.models.retrofit

import com.bubllbub.exchangerates.enums.CurrencyRes
import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.CurrencyFavorite
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import java.util.*

class CurrencyFavoriteApiData: DataSource<CurrencyFavorite> {
    private val jSONApi = APIService.instance.getJSONApi()
    private val startingCurrenciesAbbreviations = listOf("USD", "EUR", "RUB")

    override fun getAll(): Flowable<List<CurrencyFavorite>> {
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
                    it.map { obj -> CurrencyFavorite(currency = obj as Currency) }
                } as Observable<List<CurrencyFavorite>>
            }
            .toFlowable(BackpressureStrategy.LATEST)
    }

    override fun getAll(query: DataSource.Query<CurrencyFavorite>): Flowable<List<CurrencyFavorite>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(query: DataSource.Query<CurrencyFavorite>): Observable<CurrencyFavorite> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveAll(list: List<CurrencyFavorite>): Completable {
        TODO("not implemented")
    }

    override fun save(item: CurrencyFavorite): Completable {
        TODO("not implemented")
    }

    override fun delete(item: CurrencyFavorite): Completable {
        TODO("not implemented")
    }
}