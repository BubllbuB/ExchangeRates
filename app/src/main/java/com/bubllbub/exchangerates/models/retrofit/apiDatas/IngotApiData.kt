package com.bubllbub.exchangerates.models.retrofit.apiDatas

import com.bubllbub.exchangerates.enums.IngotRes
import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.models.retrofit.JSONNbrbAPI
import com.bubllbub.exchangerates.objects.Ingot
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class IngotApiData @Inject constructor(private val jSONApi: JSONNbrbAPI) : DataSource<Ingot> {
    override fun getAll(): Flowable<List<Ingot>> {
        val metalApi = jSONApi.getMetals()
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val ingotApi = jSONApi.getIngots(dateString)

        return metalApi.zipWith(
            ingotApi,
            BiFunction { metal: ArrayList<Ingot>, ingot: ArrayList<Ingot> ->
                metal.removeAt(metal.size - 1)

                val expandableIngots = mutableListOf<Ingot>()
                metal.forEach { metalElem ->
                    val firstIngot = ingot.find { it.metalIdApi == metalElem.ingotId }
                    val ingotRates =
                        ingot.filter { it != firstIngot && it.metalIdApi == metalElem.ingotId }
                            .map {
                                it.id = "${it.metalIdApi}_${it.nominal}"
                                it.ingotId = metalElem.ingotId
                                it.ingotName = metalElem.ingotName
                                it.ingotNameBel = metalElem.ingotNameBel
                                it.ingotNameEng = metalElem.ingotNameEng
                                it.symbol = IngotRes.valueOf(it.ingotNameEng).getSymbolRes()
                                it
                            }

                    firstIngot?.let {
                        it.id = "${it.metalIdApi}_${it.nominal}"
                        it.ingotId = metalElem.ingotId
                        it.ingotName = metalElem.ingotName
                        it.ingotNameBel = metalElem.ingotNameBel
                        it.ingotNameEng = metalElem.ingotNameEng
                        it.symbol = IngotRes.valueOf(it.ingotNameEng).getSymbolRes()
                        it.rates = ingotRates

                        expandableIngots.add(it)
                    }
                }
                expandableIngots.toList()
            }).toFlowable(BackpressureStrategy.LATEST)
    }

    override fun getAll(query: DataSource.Query<Ingot>): Flowable<List<Ingot>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(query: DataSource.Query<Ingot>): Observable<Ingot> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(item: Ingot): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveAll(list: List<Ingot>): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(item: Ingot): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}