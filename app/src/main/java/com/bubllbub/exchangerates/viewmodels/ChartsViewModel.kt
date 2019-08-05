package com.bubllbub.exchangerates.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bubllbub.exchangerates.models.Repository
import com.bubllbub.exchangerates.models.Repository.CUR_ABBREVIATION
import com.bubllbub.exchangerates.models.Repository.CUR_ID
import com.bubllbub.exchangerates.models.Repository.END_DATE
import com.bubllbub.exchangerates.models.Repository.START_DATE
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Rate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import java.text.SimpleDateFormat
import java.util.*


class ChartsViewModel : ViewModel() {
    var currencies = MutableLiveData<List<Currency>>()
    var rates = MutableLiveData<List<Rate>>()
    var isLoading = ObservableField(true)
    private val rateRepo = Repository.of<Rate>()
    private val currencyRepo = Repository.of<Currency>()
    private val compositeDisposable = CompositeDisposable()

    fun refresh(currencyId: Int, currencyAbbreviation: String, startDate: Date, finishDate: Date) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        compositeDisposable.add(

            rateRepo.query()
                .where(CUR_ID, currencyId.toString())
                .where(CUR_ABBREVIATION, currencyAbbreviation)
                .where(START_DATE, dateFormat.format(startDate))
                .where(END_DATE, dateFormat.format(finishDate))
                .findAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSubscriber<List<Rate>>() {
                    override fun onComplete() {
                        Log.d(ContentValues.TAG, "[onCompleted] ")
                    }

                    override fun onError(t: Throwable) {
                        Log.d(ContentValues.TAG, "[onError] ")
                        t.printStackTrace()
                    }

                    override fun onNext(m: List<Rate>) {
                        rates.value = m
                        isLoading.set(false)
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