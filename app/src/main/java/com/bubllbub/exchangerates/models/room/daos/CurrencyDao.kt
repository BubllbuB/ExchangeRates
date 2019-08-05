package com.bubllbub.exchangerates.models.room.daos

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.bubllbub.exchangerates.models.room.RoomDateConverter
import com.bubllbub.exchangerates.objects.Currency
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.*

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(currency: Currency): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(currency: List<Currency>): Completable

    @Delete
    fun delete(currency: Currency): Completable

    @Query("SELECT * FROM currency WHERE date LIKE :date")
    fun getCurrenciesOnDate(@TypeConverters(RoomDateConverter::class) date: Date): Flowable<List<Currency>>

    @RawQuery(observedEntities = [Currency::class])
    fun rawQuery(query: SupportSQLiteQuery): Flowable<List<Currency>>

    @RawQuery(observedEntities = [Currency::class])
    fun getWithQuery(query: SupportSQLiteQuery): Single<Currency>

    @Query("SELECT * FROM currency")
    fun getAll(): Flowable<List<Currency>>
}