package com.bubllbub.exchangerates.models.room

import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.objects.CurrencyFavorite
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

class CurrencyFavoriteRoomData(private val dao: CurrencyFavoritesDao) :
    DataSource<CurrencyFavorite> {
    private val tableName = "currencyFavorites"

    override fun getAll(): Flowable<List<CurrencyFavorite>> {
        return dao.getAll()
    }

    override fun getAll(query: DataSource.Query<CurrencyFavorite>): Flowable<List<CurrencyFavorite>> {
        return dao.rawQuery(RoomData.sqlWhere(tableName, query.params))
    }

    override fun get(query: DataSource.Query<CurrencyFavorite>): Observable<CurrencyFavorite> {
        return dao.getWithQuery(RoomData.sqlWhere(tableName, query.params))
    }

    override fun save(item: CurrencyFavorite): Completable {
        return dao.insert(item)
    }

    override fun saveAll(list: List<CurrencyFavorite>): Completable {
        return dao.insertAll(list)
    }

    override fun delete(item: CurrencyFavorite): Completable {
        return dao.delete(item.currency.curId)
    }
}