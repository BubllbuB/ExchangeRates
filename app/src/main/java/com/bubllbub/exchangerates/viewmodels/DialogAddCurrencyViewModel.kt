package com.bubllbub.exchangerates.viewmodels

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bubllbub.exchangerates.models.retrofit.APICurrencyModel
import com.bubllbub.exchangerates.models.room.AppDatabase
import com.bubllbub.exchangerates.objects.Currency
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class DialogAddCurrencyViewModel : ViewModel() {
    var currencyModel: APICurrencyModel =
        APICurrencyModel()
    var currencies = MutableLiveData<MutableList<Currency>>()
    var isLoading = ObservableField(true)
    private val compositeDisposable = CompositeDisposable()

    fun refresh(context: Context) {
        val appDatabase = AppDatabase.getDatabase(context)

        compositeDisposable.add(
            currencyModel.getActualCurrenciesForDialog(appDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<MutableList<Currency>>() {
                    override fun onComplete() {
                        Log.d(TAG, "[onCompleted] ")
                    }

                    override fun onError(t: Throwable) {
                        Log.d(TAG, "[onError] ")
                        t.printStackTrace()
                    }

                    override fun onNext(m: MutableList<Currency>) {
                        currencies.value = m
                        isLoading.set(false)
                        Log.d(TAG, "[onNext] " + m.toString())
                    }
                })
        )
    }

    fun addFavoriteCurrency(context: Context, currency: Currency) {
        val appDatabase = AppDatabase.getDatabase(context)
        compositeDisposable.add(
            currencyModel.insertCurrency(currency, appDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        isLoading.set(false)
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, "[onError] ")
                        e.printStackTrace()
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

    fun addConverterCurrency(context: Context, curr: Currency) {

    }
}