package com.bubllbub.exchangerates.di

import android.content.Context
import com.bubllbub.exchangerates.App
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun providesContext(application: App): Context {
        return application.applicationContext
    }
}