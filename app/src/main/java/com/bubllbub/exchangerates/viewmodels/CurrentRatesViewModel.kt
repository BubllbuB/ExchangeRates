package com.bubllbub.exchangerates.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bubllbub.exchangerates.models.Repository
import com.bubllbub.exchangerates.models.Repository.CUR_FAVORITE
import com.bubllbub.exchangerates.models.Repository.CUR_QUERY_TRUE
import com.bubllbub.exchangerates.objects.Currency
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber


class CurrentRatesViewModel : ViewModel() {
    var currencies = MutableLiveData<List<Currency>>()
    var isLoading = ObservableField(true)
    private val currencyRepo = Repository.of<Currency>()
    private val compositeDisposable = CompositeDisposable()

    fun getFavorites() {
        currencyRepo.query()
            .where(CUR_FAVORITE, CUR_QUERY_TRUE)
            .findAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSubscriber<List<Currency>>() {
                override fun onComplete() {
                    Log.d(TAG, "[onCompleted] ")
                }

                override fun onError(t: Throwable) {
                    Log.d(TAG, "[onError] ")
                    t.printStackTrace()
                }

                override fun onNext(t: List<Currency>) {
                    currencies.value = t.sortedBy { it.favoritePos }
                    isLoading.set(false)

                    Log.d(TAG, "[onSuccess] " + t.toString())
                }
            })
    }

    fun deleteFavCurrency(currency: Currency) {
        val savedCurr = currency.copy()
        savedCurr.favoritePos = 0
        savedCurr.isFavorite = false
        compositeDisposable.add(
            currencyRepo.save(savedCurr)
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

    fun insertFavCurrency(currency: Currency) {
        currency.isFavorite = true
        compositeDisposable.add(
            currencyRepo.save(currency)
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