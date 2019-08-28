package com.bubllbub.exchangerates.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.di.DaggerAppComponent
import com.bubllbub.exchangerates.di.modules.AppModule
import com.bubllbub.exchangerates.di.modules.RepositoryModule
import com.bubllbub.exchangerates.dialogs.TAG_FAVORITES
import com.bubllbub.exchangerates.enums.CurrencyRes
import com.bubllbub.exchangerates.extensions.putInCompositeDisposible
import com.bubllbub.exchangerates.models.*
import com.bubllbub.exchangerates.objects.Currency
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class DialogAddCurrencyViewModel @Inject constructor() : ViewModel() {
    @field:Inject
    lateinit var currencyRepo: Repo<Currency>
    private val _currencies = MutableLiveData<List<Currency>>()
    val currencies: LiveData<List<Currency>>
        get() = _currencies
    var isLoading = ObservableField(true)
    private val compositeDisposable = CompositeDisposable()

    init {
        DaggerAppComponent.builder()
            .appModule(AppModule(App.instance))
            .repositoryModule(RepositoryModule())
            .build()
            .inject(this)
    }

    fun refresh(tag: String?) {
        if (_currencies.value.isNullOrEmpty()) {
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
                        _currencies.value =
                            m.sortedBy { CurrencyRes.valueOf(it.curAbbreviation).ordinal }
                        isLoading.set(false)
                        Log.d(TAG, "[onNext] $m")
                    }
                })
                .putInCompositeDisposible(compositeDisposable)
        }
    }

    fun addFavoriteCurrency(curr: Currency) {
        currencyRepo.query()
            .where(CUR_FAVORITE, CUR_QUERY_TRUE)
            .findAll()
            .firstOrError()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<Currency>>() {
                override fun onSuccess(list: List<Currency>) {
                    list.maxBy { it.favoritePos }?.favoritePos?.let {
                        curr.favoritePos = it + 1
                    }
                    curr.isFavorite = true
                    currencyRepo.save(curr).subscribe()
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "[onError] ")
                    e.printStackTrace()
                }
            })
            .putInCompositeDisposible(compositeDisposable)
    }

    fun addConverterCurrency(curr: Currency) {
        currencyRepo.query()
            .where(CUR_CONVERTER, CUR_QUERY_TRUE)
            .findAll()
            .firstOrError()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<Currency>>() {
                override fun onSuccess(list: List<Currency>) {
                    list.maxBy { it.converterPos }?.converterPos?.let {
                        curr.converterPos = it + 1
                    }
                    curr.isConverter = true
                    currencyRepo.save(curr).subscribe()
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "[onError] ")
                    e.printStackTrace()
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