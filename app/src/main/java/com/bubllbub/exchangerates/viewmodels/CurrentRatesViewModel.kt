package com.bubllbub.exchangerates.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.di.DaggerAppComponent
import com.bubllbub.exchangerates.di.modules.AppModule
import com.bubllbub.exchangerates.di.modules.RepositoryModule
import com.bubllbub.exchangerates.di.modules.RetrofitModule
import com.bubllbub.exchangerates.di.modules.RoomModule
import com.bubllbub.exchangerates.models.CUR_FAVORITE
import com.bubllbub.exchangerates.models.CUR_QUERY_TRUE
import com.bubllbub.exchangerates.models.Repo
import com.bubllbub.exchangerates.objects.Currency
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class CurrentRatesViewModel @Inject constructor(): ViewModel() {
    var currencies = MutableLiveData<List<Currency>>()
    var isLoading = ObservableField(true)
    var updateString = ObservableField("")
    @Inject
    lateinit var currencyRepo: Repo<Currency>
    private val compositeDisposable = CompositeDisposable()

    init {
        DaggerAppComponent.builder()
            .appModule(AppModule(App.instance))
            .repositoryModule(RepositoryModule())
            .build()
            .inject(this)
    }

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
                    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                    updateString.set(dateFormat.format(t[0].date))
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