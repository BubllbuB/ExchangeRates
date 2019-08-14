package com.bubllbub.exchangerates.models.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bubllbub.exchangerates.models.room.daos.CurrencyDao
import com.bubllbub.exchangerates.models.room.daos.IngotDao
import com.bubllbub.exchangerates.models.room.daos.RateDao
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Ingot
import com.bubllbub.exchangerates.objects.Rate

@Database(entities = [Currency::class, Ingot::class, Rate::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    abstract fun ingotDao(): IngotDao
    abstract fun rateDao(): RateDao
}