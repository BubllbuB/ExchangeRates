package com.bubllbub.exchangerates.models.room

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.bubllbub.exchangerates.objects.CurrencyFavorite
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable

@Dao
interface CurrencyFavoritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(currency: CurrencyFavorite): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(currency: List<CurrencyFavorite>): Completable

    @Query("DELETE FROM currencyFavorites WHERE curId=:curId")
    fun delete(curId: Int): Completable

    @Query("SELECT * FROM currencyFavorites")
    fun getAll(): Flowable<List<CurrencyFavorite>>

    @Query("SELECT * FROM currencyFavorites")
    fun checkFirstInitFavoritesCurrency(): Maybe<List<CurrencyFavorite>>

    @RawQuery(observedEntities = [CurrencyFavorite::class])
    fun rawQuery(query: SupportSQLiteQuery): Flowable<List<CurrencyFavorite>>

    @RawQuery(observedEntities = [CurrencyFavorite::class])
    fun getWithQuery(query: SupportSQLiteQuery): Observable<CurrencyFavorite>
}