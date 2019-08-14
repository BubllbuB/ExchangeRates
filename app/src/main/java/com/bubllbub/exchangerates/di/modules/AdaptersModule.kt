package com.bubllbub.exchangerates.di.modules

import com.bubllbub.exchangerates.adapters.ConverterRecyclerAdapter
import com.bubllbub.exchangerates.adapters.CurrencyRecyclerAdapter
import com.bubllbub.exchangerates.adapters.DialogRecyclerAdapter
import com.bubllbub.exchangerates.adapters.IngotsRecyclerAdapter
import dagger.Module
import dagger.Provides

@Module
class AdaptersModule {
    @Provides
    fun provideCurrencyRecyclerAdapter(): CurrencyRecyclerAdapter = CurrencyRecyclerAdapter(
        mutableListOf()
    )

    @Provides
    fun provideIngotsRecyclerAdapter(): IngotsRecyclerAdapter = IngotsRecyclerAdapter(mutableListOf())

    @Provides
    fun provideConverterRecyclerAdapter(): ConverterRecyclerAdapter = ConverterRecyclerAdapter(mutableListOf())

    @Provides
    fun provideDialogRecyclerAdapter(): DialogRecyclerAdapter = DialogRecyclerAdapter(mutableListOf())
}