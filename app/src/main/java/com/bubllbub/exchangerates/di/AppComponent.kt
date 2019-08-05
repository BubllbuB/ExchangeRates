package com.bubllbub.exchangerates.di

import com.bubllbub.exchangerates.App
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class]
)
interface AppComponent {
    fun inject(application: App)
}