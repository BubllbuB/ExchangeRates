package com.bubllbub.exchangerates.models.retrofit

import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Ingot
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JSONNbrbAPI {
    //Currencies
    @GET("/API/ExRates/Currencies/{Cur_ID}")
    fun getCurrencyWithID(@Path("Cur_ID") id: Int): Observable<Currency>

    @GET("/API/ExRates/Rates/{Cur_ID}")
    fun getRatesWithID(@Path("Cur_ID") id: Int): Observable<Currency>

    @GET("/API/ExRates/Currencies")
    fun getCurrencies(): Observable<ArrayList<Currency>>

    @GET("/API/ExRates/Rates")
    fun getRatesOnDate(@Query("onDate") date: String, @Query("Periodicity") periodicity: Int): Observable<ArrayList<Currency>>

    @GET("/API/ExRates/Rates/{Cur_Name}")
    fun getRatesOnDateWithName(@Path("Cur_Name") name: String, @Query("onDate") date: String, @Query("ParamMode") mode: Int = 2): Observable<Currency>

    @GET("/API/ExRates/Rates/Dynamics/{Cur_ID}")
    fun getDynamicsRate(@Path("Cur_ID") id: Int, @Query("startDate") startDate: String, @Query("endDate") endDate: String): Observable<ArrayList<Currency>>

    //Ingots
    @GET("/API/Metals")
    fun getMetals(): Observable<ArrayList<Ingot>>

    @GET("/API/Ingots/Prices")
    fun getIngots(@Query("onDate") date: String): Observable<ArrayList<Ingot>>
}