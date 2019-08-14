package com.bubllbub.exchangerates.di

import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.di.modules.AppModule
import com.bubllbub.exchangerates.di.modules.RepositoryModule
import com.bubllbub.exchangerates.di.modules.RetrofitModule
import com.bubllbub.exchangerates.di.modules.RoomModule
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
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, AppModule::class, RetrofitModule::class, RoomModule::class, RepositoryModule::class]
)
interface AppComponent {
    fun inject(application: App)

    fun inject(activity: MainActivity)

    fun inject(fragment: BackDropFragment)

    fun inject(viewModel: CurrentRatesViewModel)
    fun inject(viewModel: ChartsViewModel)
    fun inject(viewModel: ConverterViewModel)
    fun inject(viewModel: DialogAddCurrencyViewModel)
    fun inject(viewModel: IngotsViewModel)
    fun inject(viewModel: RateOnDateViewModel)

    fun inject(dataSource: CurrencyApiData)
    fun inject(dataSource: IngotApiData)
    fun inject(dataSource: RateApiData)

    fun inject(dataSource: CurrencyRoomData)
    fun inject(dataSource: IngotRoomData)
    fun inject(dataSource: RateRoomData)

    fun inject(worker: UpdateDatabasesWorker)
}