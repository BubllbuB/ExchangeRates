package com.bubllbub.exchangerates.models.retrofit

import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.objects.Ingot
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

class IngotApiData : DataSource<Ingot> {
    private val jSONApi = APIService.instance.getJSONApi()

    override fun getAll(): Flowable<List<Ingot>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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