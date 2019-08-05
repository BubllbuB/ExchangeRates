package com.bubllbub.exchangerates.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bubllbub.exchangerates.dialogs.TAG_FAVORITES
import com.bubllbub.exchangerates.enums.CurrencyRes
import com.bubllbub.exchangerates.models.Repository
import com.bubllbub.exchangerates.models.Repository.CUR_CONVERTER
import com.bubllbub.exchangerates.models.Repository.CUR_FAVORITE
import com.bubllbub.exchangerates.models.Repository.CUR_QUERY_FALSE
import com.bubllbub.exchangerates.models.Repository.CUR_QUERY_TRUE
import com.bubllbub.exchangerates.models.Repository.DIALOG_CUR
import com.bubllbub.exchangerates.objects.Currency
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber

class DialogAddCurrencyViewModel : ViewModel() {
    private val currencyRepo = Repository.of<Currency>()
    var currencies = MutableLiveData<List<Currency>>()
    var isLoading = ObservableField(true)
    private val compositeDisposable = CompositeDisposable()

    fun refresh(tag: String?) {
        compositeDisposable.add(
            currencyRepo.query()
                .where(
                    if (tag == TAG_FAVORITES) CUR_FAVORITE else CUR_CONVERTER,
                    CUR_QUERY_FALSE
                )
                .where(DIALOG_CUR, DIALOG_CUR)
                .findAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSubscriber<List<Currency>>() {
                    override fun onComplete() {
                        Log.d(TAG, "[onCompleted] ")
                    }

                    override fun onError(t: Throwable) {
                        Log.d(TAG, "[onError] ")
                        t.printStackTrace()
                    }

                    override fun onNext(m: List<Currency>) {
                        currencies.value =
                            m.sortedBy { CurrencyRes.valueOf(it.curAbbreviation).ordinal }
                        isLoading.set(false)
                        Log.d(TAG, "[onNext] " + m.toString())
                    }
                })
        )
    }

    fun addFavoriteCurrency(curr: Currency) {
        compositeDisposable.add(
            currencyRepo.query()
                .where(CUR_FAVORITE, CUR_QUERY_TRUE)
                .findAll()
                .flatMapCompletable { list ->
                    list.maxBy { it.favoritePos }?.favoritePos?.let {
                        curr.favoritePos = it + 1
                    }
                    curr.isFavorite = true
                    currencyRepo.save(curr).subscribe()
                    Completable.fromAction {  }
                }
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

    fun addConverterCurrency(curr: Currency) {
        compositeDisposable.add(
            currencyRepo.query()
                .where(CUR_CONVERTER, CUR_QUERY_TRUE)
                .findAll()
                .flatMapCompletable { list ->
                    list.maxBy { it.converterPos }?.converterPos?.let {
                        curr.converterPos = it + 1
                    }
                    curr.isConverter = true
                    currencyRepo.save(curr).subscribe()
                    Completable.fromAction {  }
                }
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