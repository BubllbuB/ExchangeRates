package com.bubllbub.exchangerates.di

import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.di.modules.*
import com.bubllbub.exchangerates.models.retrofit.apiDatas.CurrencyApiData
import com.bubllbub.exchangerates.models.retrofit.apiDatas.IngotApiData
import com.bubllbub.exchangerates.models.retrofit.apiDatas.RateApiData
import com.bubllbub.exchangerates.models.room.roomDatas.CurrencyRoomData
import com.bubllbub.exchangerates.models.room.roomDatas.IngotRoomData
import com.bubllbub.exchangerates.models.room.roomDatas.RateRoomData
import com.bubllbub.exchangerates.viewmodels.*
import com.bubllbub.exchangerates.views.MainActivity
import com.bubllbub.exchangerates.views.fragments.BackDropFragment
import com.bubllbub.exchangerates.workers.UpdateDatabasesWorker
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, ActivityModule::class, FragmentModule::class, AppModule::class, RetrofitModule::class, RoomModule::class, RepositoryModule::class, AdaptersModule::class]
)
interface AppComponent : AndroidInjector<App> {
    fun inject(viewModel: CurrentRatesViewModel)
    fun inject(viewModel: ChartsViewModel)
    fun inject(viewModel: ConverterViewModel)
    fun inject(viewModel: DialogAddCurrencyViewModel)
    fun inject(viewModel: IngotsViewModel)
    fun inject(viewModel: RateOnDateViewModel)

    fun inject(worker: UpdateDatabasesWorker)
}
