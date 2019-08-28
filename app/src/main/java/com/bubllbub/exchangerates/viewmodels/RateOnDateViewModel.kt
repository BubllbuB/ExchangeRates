package com.bubllbub.exchangerates.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.di.DaggerAppComponent
import com.bubllbub.exchangerates.di.modules.AppModule
import com.bubllbub.exchangerates.di.modules.RepositoryModule
import com.bubllbub.exchangerates.enums.CurrencyRes
import com.bubllbub.exchangerates.extensions.putInCompositeDisposible
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

class RateOnDateViewModel @Inject constructor() : ViewModel() {
    var currency =
        ObservableField(Currency(curDateEnd = Date(), curDateStart = Date(), date = Date()))
    private val _currencies = MutableLiveData<List<Currency>>()
    val currencies: LiveData<List<Currency>>
        get() = _currencies
    var date = ObservableField(DateTime().withTimeAtStartOfDay())
    private val compositeDisposable = CompositeDisposable()

    @field:Inject
    lateinit var currencyRepo: Repo<Currency>

    init {
        DaggerAppComponent.builder()
            .appModule(AppModule(App.instance))
            .repositoryModule(RepositoryModule())
            .build()
            .inject(this)
    }

    fun refresh(currencyAbbreviation: String) {
        val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")

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
                    Log.d(ContentValues.TAG, "[onNext] $m")
                }
            })
            .putInCompositeDisposible(compositeDisposable)
    }

    fun getActualList() {
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
                    _currencies.value =
                        m.sortedBy { CurrencyRes.valueOf(it.curAbbreviation).ordinal }
                    Log.d(ContentValues.TAG, "[onNext] $m")
                }
            })
            .putInCompositeDisposible(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }
}