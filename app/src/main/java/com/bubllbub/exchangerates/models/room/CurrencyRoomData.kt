package com.bubllbub.exchangerates.models.room

import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.models.room.RoomData.sqlWhere
import com.bubllbub.exchangerates.objects.Currency
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

class CurrencyRoomData(private val dao: CurrencyDao) : DataSource<Currency> {
    private val tableName = "currency"

    override fun getAll(): Flowable<List<Currency>> {
        return dao.getAll()
    }

    override fun getAll(query: DataSource.Query<Currency>): Flowable<List<Currency>> {
        return dao.rawQuery(sqlWhere(tableName, query.params))
    }

    override fun get(query: DataSource.Query<Currency>): Observable<Currency> {
        return dao.getWithQuery(sqlWhere(tableName, query.params))
    }

    override fun save(item: Currency): Completable {
        return dao.insert(item)
    }

    override fun saveAll(list: List<Currency>): Completable {
        return dao.insertAll(list)
    }

    override fun delete(item: Currency): Completable {
        return dao.delete(item)
    }
}