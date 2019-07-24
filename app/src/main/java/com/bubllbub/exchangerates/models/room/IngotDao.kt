package com.bubllbub.exchangerates.models.room

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.bubllbub.exchangerates.objects.Ingot
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

@Dao
interface IngotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ingot: Ingot): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(ingots: List<Ingot>): Completable

    @Delete
    fun delete(ingot: Ingot): Completable

    @RawQuery(observedEntities = [Ingot::class])
    fun rawQuery(query: SupportSQLiteQuery): Flowable<List<Ingot>>

    @RawQuery(observedEntities = [Ingot::class])
    fun getWithQuery(query: SupportSQLiteQuery): Observable<Ingot>

    @Query("SELECT * FROM ingots")
    fun getAll(): Flowable<List<Ingot>>
}