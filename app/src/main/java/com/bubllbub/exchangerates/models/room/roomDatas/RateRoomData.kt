package com.bubllbub.exchangerates.models.room.roomDatas

import com.bubllbub.exchangerates.models.CUR_ID
import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.models.END_DATE
import com.bubllbub.exchangerates.models.START_DATE
import com.bubllbub.exchangerates.models.room.RoomData.sqlBetween
import com.bubllbub.exchangerates.models.room.RoomData.sqlWhere
import com.bubllbub.exchangerates.models.room.daos.RateDao
import com.bubllbub.exchangerates.objects.Rate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import javax.inject.Inject

class RateRoomData @Inject constructor(private val dao: RateDao) : DataSource<Rate> {
    private val tableName = "rates"

    override fun getAll(): Flowable<List<Rate>> {
        return dao.getAll()
    }

    override fun getAll(query: DataSource.Query<Rate>): Flowable<List<Rate>> {
        return when {
            (query.has(CUR_ID) && query.has(START_DATE) && query.has(
                END_DATE
            )) -> {
                dao.rawQuery(sqlBetween(tableName, query.params)).toFlowable()
            }
            else -> dao.rawQuery(sqlWhere(tableName, query.params)).toFlowable()
        }
    }

    override fun get(query: DataSource.Query<Rate>): Observable<Rate> {
        return dao.getWithQuery(sqlWhere(tableName, query.params))
    }

    override fun save(item: Rate): Completable {
        return dao.insert(item)
    }

    override fun saveAll(list: List<Rate>): Completable {
        return dao.insertAll(list)
    }

    override fun delete(item: Rate): Completable {
        return dao.delete(item)
    }
}