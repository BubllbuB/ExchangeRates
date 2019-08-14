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
import com.bubllbub.exchangerates.enums.CurrencyRes
import com.bubllbub.exchangerates.models.CUR_CONVERTER
import com.bubllbub.exchangerates.models.CUR_QUERY_TRUE
import com.bubllbub.exchangerates.models.Repo
import com.bubllbub.exchangerates.objects.Currency
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class ConverterViewModel : ViewModel() {
    var currencies = MutableLiveData<List<Currency>>()
    var isLoading = ObservableField(true)
    private val compositeDisposable = CompositeDisposable()
    @Inject
    lateinit var currencyRepo: Repo<Currency>
    private val BYN = Currency(
        curOfficialRate = 1.0,
        curAbbreviation = "BYN",
        curName = "Белорусский рубль",
        curNameBel = "Беларускі рубель",
        curNameEng = "Belarusian ruble",
        curQuotName = "1 Белорусский рубль",
        curQuotNameEng = "1 Belarusian Ruble",
        curQuotNameBel = "1 Беларускі рубель",
        symbol = CurrencyRes.valueOf("BYN").getSymbolRes(),
        isConverter = true,
        converterPos = 0
    )

    init {
        DaggerAppComponent.builder()
            .appModule(AppModule(App.instance))
            .repositoryModule(RepositoryModule())
            .build()
            .inject(this)
    }

    fun getCurrenciesForConverter() {
        compositeDisposable.add(
            currencyRepo.query()
                .where(CUR_CONVERTER, CUR_QUERY_TRUE)
                .findAll()
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
                        val convertList = mutableListOf(BYN)
                        convertList.addAll(m)
                        currencies.value = convertList.sortedBy { it.converterPos }
                        isLoading.set(false)
                        Log.d(ContentValues.TAG, "[onNext] " + m.toString())
                    }
                })
        )
    }

    fun deleteConverterCurrency(currency: Currency) {
        val savedCurr = currency.copy()
        savedCurr.converterPos = 0
        savedCurr.isConverter = false
        compositeDisposable.add(
            currencyRepo.save(savedCurr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        isLoading.set(false)
                    }

                    override fun onError(e: Throwable) {
                        Log.d(ContentValues.TAG, "[onError] ")
                        e.printStackTrace()
                    }
                })
        )

    }

    fun insertConverterCurrency(currency: Currency) {
        currency.isConverter = true
        compositeDisposable.add(
            currencyRepo.save(currency)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        isLoading.set(false)
                    }

                    override fun onError(e: Throwable) {
                        Log.d(ContentValues.TAG, "[onError] ")
                        e.printStackTrace()
                    }
                })
        )
    }
}