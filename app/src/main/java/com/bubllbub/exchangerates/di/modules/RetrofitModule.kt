package com.bubllbub.exchangerates.di.modules

import com.bubllbub.exchangerates.converters.DateTimeConverter
import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.models.retrofit.JSONNbrbAPI
import com.bubllbub.exchangerates.models.retrofit.apiDatas.CurrencyApiData
import com.bubllbub.exchangerates.models.retrofit.apiDatas.IngotApiData
import com.bubllbub.exchangerates.models.retrofit.apiDatas.RateApiData
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Ingot
import com.bubllbub.exchangerates.objects.Rate
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "http://www.nbrb.by/"

@Module
class RetrofitModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(DateTime::class.java, DateTimeConverter())
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideJSONNbrbAPI(retrofit: Retrofit): JSONNbrbAPI = retrofit.create(JSONNbrbAPI::class.java)

    @Provides
    @Singleton
    fun provideCurrencyAPI(jSONAPI: JSONNbrbAPI): DataSource<Currency> = CurrencyApiData(jSONAPI)

    @Provides
    @Singleton
    fun provideIngotAPI(jSONAPI: JSONNbrbAPI): DataSource<Ingot> = IngotApiData(jSONAPI)

    @Provides
    @Singleton
    fun provideRateAPI(jSONAPI: JSONNbrbAPI): DataSource<Rate> = RateApiData(jSONAPI)
}