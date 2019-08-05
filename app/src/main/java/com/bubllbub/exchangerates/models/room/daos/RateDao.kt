package com.bubllbub.exchangerates.models.room.daos

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.bubllbub.exchangerates.objects.Rate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface RateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rate: Rate): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(rates: List<Rate>): Completable

    @Delete
    fun delete(rate: Rate): Completable

    @RawQuery(observedEntities = [Rate::class])
    fun rawQuery(query: SupportSQLiteQuery): Single<List<Rate>>

    @RawQuery(observedEntities = [Rate::class])
    fun getWithQuery(query: SupportSQLiteQuery): Observable<Rate>

    @Query("SELECT * FROM rates")
    fun getAll(): Flowable<List<Rate>>
}