package com.bubllbub.exchangerates.viewmodels

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bubllbub.exchangerates.enums.CurrencyRes
import com.bubllbub.exchangerates.models.retrofit.APICurrencyModel
import com.bubllbub.exchangerates.models.room.AppDatabase
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.CurrencyFavorite
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber


class CurrentRatesViewModel : ViewModel() {
    var currencyModel: APICurrencyModel =
        APICurrencyModel()
    var currencies = MutableLiveData<List<Currency>>()
    var isLoading = ObservableField(true)
    private val compositeDisposable = CompositeDisposable()
    private val ids = arrayListOf(145, 292, 298)

    fun firstSetupFavorites(context: Context) {
        val appDatabase = AppDatabase.getDatabase(context)
        compositeDisposable.add(
            currencyModel.getStartingCurrencies(appDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<List<Currency>>() {
                    override fun onComplete() {
                        Log.d(TAG, "[onCompleted] ")
                    }

                    override fun onError(t: Throwable) {
                        Log.d(TAG, "[onError] ")
                        t.printStackTrace()
                    }

                    override fun onNext(m: List<Currency>) {
                        currencies.value = m
                        isLoading.set(false)
                        Log.d(TAG, "[onNext] " + m.toString())
                    }
                })
        )
    }

    /*fun getCurrenciesForConverter(context: Context) {
        val appDatabase = AppDatabase.getDatabase(context)
        appDatabase.currencyFavoritesDao().checkFirstInitFavoritesCurrency()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : MaybeObserver<List<CurrencyFavorite>> {
                override fun onComplete() {
                    Log.d(TAG, "[onCompleted] ")
                }

                override fun onError(t: Throwable) {
                    Log.d(TAG, "[onError] ")
                    t.printStackTrace()
                }

                override fun onSuccess(t: List<CurrencyFavorite>) {
                    if (t.isNullOrEmpty()) {
                        firstSetupFavorites(context)
                    } else {
                        val currenciesFavorites = t.map { it.currency }
                        currenciesFavorites.forEach {
                            it.symbol = CurrencyRes.valueOf(it.curAbbreviation).getSymbolRes()
                        }
                        currencies.value = currenciesFavorites
                        isLoading.set(false)
                    }
                    Log.d(TAG, "[onSuccess] " + t.toString())
                }

                override fun onSubscribe(s: Disposable) {
                    Log.d(TAG, "[onSubscribe] ")
                }
            }
            )
    }*/

    fun getFavorites(context: Context) {
        val appDatabase = AppDatabase.getDatabase(context)
        appDatabase.currencyFavoritesDao().getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSubscriber<List<CurrencyFavorite>>() {
                override fun onComplete() {
                    Log.d(TAG, "[onCompleted] ")
                }

                override fun onError(t: Throwable) {
                    Log.d(TAG, "[onError] ")
                    t.printStackTrace()
                }

                override fun onNext(t: List<CurrencyFavorite>?) {
                    if (t.isNullOrEmpty()) {
                        firstSetupFavorites(context)
                    } else {
                        val currenciesFavorites = t.map { it.currency }
                        currenciesFavorites.forEach {
                            it.symbol = CurrencyRes.valueOf(it.curAbbreviation).getSymbolRes()
                        }
                        currencies.value = currenciesFavorites
                        isLoading.set(false)
                    }
                    Log.d(TAG, "[onSuccess] " + t.toString())
                }
            }
            )
    }

    fun deleteFavCurrency(currencyFavorite: CurrencyFavorite, context: Context) {
        val appDatabase = AppDatabase.getDatabase(context)
        compositeDisposable.add(
            currencyModel.deleteFavCurrency(currencyFavorite, appDatabase)
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

    fun insertFavCurrency(currencyFavorite: CurrencyFavorite, context: Context) {
        val appDatabase = AppDatabase.getDatabase(context)
        compositeDisposable.add(
            currencyModel.insertFavCurrency(currencyFavorite, appDatabase)
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
}