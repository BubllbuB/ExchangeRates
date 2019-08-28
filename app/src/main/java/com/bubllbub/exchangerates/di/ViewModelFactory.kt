package com.bubllbub.exchangerates.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bubllbub.exchangerates.viewmodels.*
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class ViewModelFactory @Inject constructor(private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        viewModels[modelClass]?.get() as T
}

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ChartsViewModel::class)
    internal abstract fun postChartsViewModel(viewModel: ChartsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConverterViewModel::class)
    internal abstract fun postConverterViewModel(viewModel: ConverterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CurrentRatesViewModel::class)
    internal abstract fun postCurrentRatesViewModel(viewModel: CurrentRatesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IngotsViewModel::class)
    internal abstract fun postIngotsViewModel(viewModel: IngotsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RateOnDateViewModel::class)
    internal abstract fun postRateOnDateViewModel(viewModel: RateOnDateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DialogAddCurrencyViewModel::class)
    internal abstract fun postDialogAddCurrencyViewModel(viewModel: DialogAddCurrencyViewModel): ViewModel
}