package com.bubllbub.exchangerates.models.retrofit

import com.bubllbub.exchangerates.converters.DateTimeConverter
import com.google.gson.GsonBuilder
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "http://www.nbrb.by/"

class APIService {
    private val mGson = GsonBuilder()
        .registerTypeAdapter(DateTime::class.java, DateTimeConverter())
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    private val mRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(mGson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    companion object {
        val instance = APIService()
    }

    fun getJSONApi(): JSONNbrbAPI {
        return mRetrofit.create(JSONNbrbAPI::class.java)
    }
}