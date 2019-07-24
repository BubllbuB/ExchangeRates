package com.bubllbub.exchangerates.models.room

import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.objects.Ingot
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

class IngotRoomData(private val dao: IngotDao) : DataSource<Ingot> {
    private val tableName = "ingots"

    override fun getAll(): Flowable<List<Ingot>> {
        return dao.getAll()
    }

    override fun getAll(query: DataSource.Query<Ingot>): Flowable<List<Ingot>> {
        return dao.rawQuery(RoomData.sqlWhere(tableName, query.params))
    }

    override fun get(query: DataSource.Query<Ingot>): Observable<Ingot> {
        return dao.getWithQuery(RoomData.sqlWhere(tableName, query.params))
    }

    override fun save(item: Ingot): Completable {
        return dao.insert(item)
    }

    override fun saveAll(list: List<Ingot>): Completable {
        return dao.insertAll(list)
    }

    override fun delete(item: Ingot): Completable {
        return dao.delete(item)
    }
}