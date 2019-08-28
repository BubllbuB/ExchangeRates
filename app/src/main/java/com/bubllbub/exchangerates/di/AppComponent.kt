package com.bubllbub.exchangerates.di

import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.di.modules.*
import com.bubllbub.exchangerates.models.Repo
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.viewmodels.*
import com.bubllbub.exchangerates.workers.UpdateDatabasesWorker
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, ActivityModule::class, FragmentModule::class, AppModule::class, ViewModelModule::class, RetrofitModule::class, RoomModule::class, RepositoryModule::class, AdaptersModule::class]
)
interface AppComponent : AndroidInjector<App> {
    fun inject(viewModel: CurrentRatesViewModel)
    fun inject(viewModel: ChartsViewModel)
    fun inject(viewModel: ConverterViewModel)
    fun inject(viewModel: DialogAddCurrencyViewModel)
    fun inject(viewModel: IngotsViewModel)
    fun inject(viewModel: RateOnDateViewModel)

    fun inject(worker: UpdateDatabasesWorker)

    fun inject(repo: Repo<Currency>)
}
