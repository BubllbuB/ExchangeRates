package com.bubllbub.exchangerates.models.room.roomDatas

import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.models.room.RoomData
import com.bubllbub.exchangerates.models.room.RoomData.sqlWhere
import com.bubllbub.exchangerates.models.room.daos.IngotDao
import com.bubllbub.exchangerates.objects.Ingot
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import javax.inject.Inject

class IngotRoomData @Inject constructor(private val dao: IngotDao) : DataSource<Ingot> {
    private val tableName = "ingots"

    override fun getAll(): Flowable<List<Ingot>> {
        return dao.getAll()
            .flatMap { list ->
                val metals = list.distinctBy { it.ingotId }
                metals.forEach { firstIngot ->
                    val ingotRates =
                        list.filter { it != firstIngot && it.ingotId == firstIngot.ingotId }
                    firstIngot.rates = ingotRates
                }
                Flowable.just(metals)
            }
    }

    override fun getAll(query: DataSource.Query<Ingot>): Flowable<List<Ingot>> {
        return dao.rawQuery(
            sqlWhere(
                tableName,
                query.params
            )
        )
    }

    override fun get(query: DataSource.Query<Ingot>): Observable<Ingot> {
        return dao.getWithQuery(
            sqlWhere(
                tableName,
                query.params
            )
        )
    }

    override fun save(item: Ingot): Completable {
        return dao.insert(item)
    }

    override fun saveAll(list: List<Ingot>): Completable {
        val dbList = mutableListOf<Ingot>()
        list.map {
            dbList.add(it)
            dbList.addAll(it.rates)
        }
        return dao.insertAll(dbList)
    }

    override fun delete(item: Ingot): Completable {
        return dao.delete(item)
    }
}