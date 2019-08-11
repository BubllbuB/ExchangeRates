package com.bubllbub.exchangerates.di

import android.app.Application
import android.content.Context
import com.bubllbub.exchangerates.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton
    fun providesContext(): Context = application
}