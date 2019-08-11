package com.bubllbub.exchangerates.di

import com.bubllbub.exchangerates.App
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, AppModule::class]
)
interface AppComponent {
    fun inject(application: App)
}