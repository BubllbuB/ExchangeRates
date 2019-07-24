package com.bubllbub.exchangerates.models.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.CurrencyFavorite
import com.bubllbub.exchangerates.objects.Ingot

@Database(entities = [Currency::class, CurrencyFavorite::class, Ingot::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    abstract fun currencyFavoritesDao(): CurrencyFavoritesDao
    abstract fun ingotDao(): IngotDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}