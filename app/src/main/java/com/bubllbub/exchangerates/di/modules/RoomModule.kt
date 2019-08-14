package com.bubllbub.exchangerates.di.modules

import android.content.Context
import androidx.room.Room
import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.models.room.AppDatabase
import com.bubllbub.exchangerates.models.room.daos.CurrencyDao
import com.bubllbub.exchangerates.models.room.daos.IngotDao
import com.bubllbub.exchangerates.models.room.daos.RateDao
import com.bubllbub.exchangerates.models.room.roomDatas.CurrencyRoomData
import com.bubllbub.exchangerates.models.room.roomDatas.IngotRoomData
import com.bubllbub.exchangerates.models.room.roomDatas.RateRoomData
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Ingot
import com.bubllbub.exchangerates.objects.Rate
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {
    @Provides
    @Singleton
    fun provideRoomDatabase(context: Context) = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "app_database"
    ).build()

    @Provides
    @Singleton
    fun provideCurrencyDao(database: AppDatabase) = database.currencyDao()

    @Provides
    @Singleton
    fun provideIngotDao(database: AppDatabase) = database.ingotDao()

    @Provides
    @Singleton
    fun provideRateDao(database: AppDatabase) = database.rateDao()

    @Provides
    @Singleton
    fun provideCurrencyRoom(dao: CurrencyDao): DataSource<Currency> = CurrencyRoomData(dao)

    @Provides
    @Singleton
    fun provideIngotRoom(dao: IngotDao): DataSource<Ingot> = IngotRoomData(dao)

    @Provides
    @Singleton
    fun provideRateRoom(dao: RateDao): DataSource<Rate> = RateRoomData(dao)
}