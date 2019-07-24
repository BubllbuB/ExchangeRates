package com.bubllbub.exchangerates.models.retrofit

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


private const val BASE_URL = "http://www.nbrb.by/"

class APIService {
    private val mRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    companion object {
        val instance = APIService()
    }

    fun getJSONApi(): JSONNbrbAPI {
        return mRetrofit.create(JSONNbrbAPI::class.java)
    }
}