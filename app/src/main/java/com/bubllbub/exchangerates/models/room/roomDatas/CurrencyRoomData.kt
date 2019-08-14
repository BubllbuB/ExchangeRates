package com.bubllbub.exchangerates.models.room.roomDatas

import com.bubllbub.exchangerates.models.CUR_ID
import com.bubllbub.exchangerates.models.DIALOG_CUR
import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.models.room.RoomData.sqlNotIn
import com.bubllbub.exchangerates.models.room.RoomData.sqlWhere
import com.bubllbub.exchangerates.models.room.daos.CurrencyDao
import com.bubllbub.exchangerates.objects.Currency
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import javax.inject.Inject

class CurrencyRoomData @Inject constructor(private val dao: CurrencyDao) : DataSource<Currency> {
    private val tableName = "currency"

    override fun getAll(): Flowable<List<Currency>> {
        return dao.getAll()
    }

    override fun getAll(query: DataSource.Query<Currency>): Flowable<List<Currency>> {
        return when {
            (query.has(DIALOG_CUR) && query.has(CUR_ID)) -> {
                dao.rawQuery(sqlNotIn(tableName, query.params))
            }
            else -> dao.rawQuery(sqlWhere(tableName, query.params))
        }
    }

    override fun get(query: DataSource.Query<Currency>): Observable<Currency> {
        return dao.getWithQuery(sqlWhere(tableName, query.params)).toObservable()
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