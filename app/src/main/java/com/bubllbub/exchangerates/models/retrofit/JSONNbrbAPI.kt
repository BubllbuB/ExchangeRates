package com.bubllbub.exchangerates.models.retrofit

import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Ingot
import com.bubllbub.exchangerates.objects.Rate
import io.reactivex.Flowable
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
    fun getCurrencies(): Flowable<ArrayList<Currency>>

    @GET("/API/ExRates/Rates")
    fun getRatesOnDate(@Query("onDate") date: String, @Query("Periodicity") periodicity: Int): Flowable<ArrayList<Rate>>

    @GET("/API/ExRates/Rates")
    fun getActualRatesDaily(@Query("Periodicity") periodicity: Int = 0): Flowable<ArrayList<Rate>>

    @GET("/API/ExRates/Rates")
    fun getActualRatesMonthly(@Query("Periodicity") periodicity: Int = 1): Flowable<ArrayList<Rate>>

    @GET("/API/ExRates/Rates/{Cur_Abbreviation}")
    fun getRatesOnDateWithName(@Path("Cur_Abbreviation") abbreviation: String, @Query("onDate") date: String, @Query("ParamMode") mode: Int = 2): Observable<Currency>

    @GET("/API/ExRates/Rates/{Cur_Id}")
    fun getRatesOnDateWithId(@Path("Cur_Id") id: Int, @Query("onDate") date: String): Observable<Rate>


    @GET("/API/ExRates/Rates/Dynamics/{Cur_ID}")
    fun getDynamicsRate(@Path("Cur_ID") id: Int, @Query("startDate") startDate: String, @Query("endDate") endDate: String): Flowable<ArrayList<Rate>>

    //Ingots
    @GET("/API/Metals")
    fun getMetals(): Observable<ArrayList<Ingot>>

    @GET("/API/Ingots/Prices")
    fun getIngots(@Query("onDate") date: String): Observable<ArrayList<Ingot>>
}