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
import com.bubllbub.exchangerates.models.Repo
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Ingot
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class IngotsViewModel @Inject constructor(): ViewModel() {
    private val _ingots = MutableLiveData<List<Ingot>>()
    val ingots: LiveData<List<Ingot>>
        get() = _ingots
    var isLoading = ObservableField(true)
    private val compositeDisposable = CompositeDisposable()
    @Inject lateinit var ingotRepo: Repo<Ingot>

    init {
        DaggerAppComponent.builder()
            .appModule(AppModule(App.instance))
            .repositoryModule(RepositoryModule())
            .build()
            .inject(this)
    }

    fun refresh() {
        compositeDisposable.add(
            ingotRepo.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSubscriber<List<Ingot>>() {
                    override fun onComplete() {
                        Log.d(ContentValues.TAG, "[onCompleted] ")
                    }

                    override fun onError(t: Throwable) {
                        Log.d(ContentValues.TAG, "[onError] ")
                        t.printStackTrace()
                    }

                    override fun onNext(m: List<Ingot>) {
                        _ingots.value = m
                        isLoading.set(false)
                        Log.d(ContentValues.TAG, "[onNext] $m")
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