package com.bubllbub.exchangerates.di.modules

import com.bubllbub.exchangerates.dialogs.AddCurrencyDialog
import com.bubllbub.exchangerates.views.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeCurrentRatesFragment(): CurrentRatesFragment

    @ContributesAndroidInjector
    abstract fun contributeChartRatesFragment(): ChartRatesFragment

    @ContributesAndroidInjector
    abstract fun contributeConverterFragment(): ConverterFragment

    @ContributesAndroidInjector
    abstract fun contributeRateOnDateFragment(): RateOnDateFragment

    @ContributesAndroidInjector
    abstract fun contributeIngotsFragment(): IngotsFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutAppFragment(): AboutAppFragment

    @ContributesAndroidInjector
    abstract fun contributeAddDialogFragment(): AddCurrencyDialog
}