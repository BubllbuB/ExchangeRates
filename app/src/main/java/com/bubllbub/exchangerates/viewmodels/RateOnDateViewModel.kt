package com.bubllbub.exchangerates.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.di.DaggerAppComponent
import com.bubllbub.exchangerates.di.modules.AppModule
import com.bubllbub.exchangerates.di.modules.RepositoryModule
import com.bubllbub.exchangerates.models.CUR_ABBREVIATION
import com.bubllbub.exchangerates.models.CUR_DATE
import com.bubllbub.exchangerates.models.DATE_IN_MILLI
import com.bubllbub.exchangerates.models.Repo
import com.bubllbub.exchangerates.objects.Currency
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*
import javax.inject.Inject

class RateOnDateViewModel : ViewModel() {
    var currency =
        ObservableField(Currency(curDateEnd = Date(), curDateStart = Date(), date = Date()))
    var currencies = MutableLiveData<List<Currency>>()
    var date = ObservableField(DateTime().withTimeAtStartOfDay())
    var isLoading = ObservableField(true)
    var isLoadingRate = ObservableField(true)
    private val compositeDisposable = CompositeDisposable()

    @Inject lateinit var currencyRepo: Repo<Currency>

    init {
        DaggerAppComponent.builder()
            .appModule(AppModule(App.instance))
            .repositoryModule(RepositoryModule())
            .build()
            .inject(this)
    }

    fun refresh(currencyAbbreviation: String) {
        val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")

        compositeDisposable.add(
            currencyRepo.query()
                .where(CUR_ABBREVIATION, currencyAbbreviation)
                .where(CUR_DATE, dateFormat.print(date.get()))
                .where(DATE_IN_MILLI, date.get()!!.millis.toString())
                .findOne()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Currency>() {
                    override fun onComplete() {
                        Log.d(ContentValues.TAG, "[onCompleted] ")
                    }

                    override fun onError(t: Throwable) {
                        Log.d(ContentValues.TAG, "[onError] ")
                        t.printStackTrace()
                    }

                    override fun onNext(m: Currency) {
                        currency.set(m)
                        isLoadingRate.set(false)
                        Log.d(ContentValues.TAG, "[onNext] " + m.toString())
                    }
                })
        )
    }

    fun getActualList() {
        compositeDisposable.add(
            currencyRepo.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSubscriber<List<Currency>>() {
                    override fun onComplete() {
                        Log.d(ContentValues.TAG, "[onCompleted] ")
                    }

                    override fun onError(t: Throwable) {
                        Log.d(ContentValues.TAG, "[onError] ")
                        t.printStackTrace()
                    }

                    override fun onNext(m: List<Currency>) {
                        currencies.value = m
                        isLoading.set(false)
                        Log.d(ContentValues.TAG, "[onNext] " + m.toString())
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }
}