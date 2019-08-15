package com.bubllbub.exchangerates.di.modules

import com.bubllbub.exchangerates.models.Repo
import com.bubllbub.exchangerates.models.retrofit.apiDatas.CurrencyApiData
import com.bubllbub.exchangerates.models.retrofit.apiDatas.IngotApiData
import com.bubllbub.exchangerates.models.retrofit.apiDatas.RateApiData
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
class RepositoryModule {

    @Provides
    @Singleton
    fun provideRepositoryCurrency(api: CurrencyApiData, database: CurrencyRoomData): Repo<Currency> =
        Repo(api, database, Currency::class)

    @Provides
    @Singleton
    fun provideRepositoryIngot(api: IngotApiData, database: IngotRoomData): Repo<Ingot> =
        Repo(api, database, Ingot::class)

    @Provides
    @Singleton
    fun provideRepositoryRate(api: RateApiData, database: RateRoomData): Repo<Rate> =
        Repo(api, database, Rate::class)
}