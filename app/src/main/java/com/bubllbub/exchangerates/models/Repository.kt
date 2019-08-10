package com.bubllbub.exchangerates.models

import com.bubllbub.exchangerates.enums.CurrencyRes
import com.bubllbub.exchangerates.models.Repository.CUR_ABBREVIATION
import com.bubllbub.exchangerates.models.Repository.CUR_DATE
import com.bubllbub.exchangerates.models.Repository.CUR_ID
import com.bubllbub.exchangerates.models.Repository.DATE_IN_MILLI
import com.bubllbub.exchangerates.models.Repository.DIALOG_CUR
import com.bubllbub.exchangerates.models.Repository.END_DATE
import com.bubllbub.exchangerates.models.Repository.START_DATE
import com.bubllbub.exchangerates.models.Repository.UPDATE_DATAS
import com.bubllbub.exchangerates.models.retrofit.NbrbApiData
import com.bubllbub.exchangerates.models.room.RoomData
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Rate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Repository {

    const val CUR_ABBREVIATION = "curAbbreviation"
    const val DATE_IN_MILLI = "rateDate"
    const val START_DATE = "startDate"
    const val END_DATE = "endDate"
    const val CUR_ID = "curId"
    const val CUR_DATE = "date"
    const val RATE_ID = "id"
    const val DIALOG_CUR = "currenciesForDialog"
    const val CUR_CONVERTER = "isConverter"
    const val CUR_FAVORITE = "isFavorite"
    const val CUR_QUERY_TRUE = "1"
    const val CUR_QUERY_FALSE = "0"
    const val UPDATE_DATAS = "updateDatas"

    inline fun <reified Entity : Any> of(): Repo<Entity> {
        return Repo(NbrbApiData.of(Entity::class), RoomData.of(Entity::class))
    }
}

class Repo<Entity : Any>(val api: DataSource<Entity>, val db: DataSource<Entity>) :
    DataSource<Entity> {
    override fun getAll(): Flowable<List<Entity>> {
        return db.getAll()
            .flatMap {
                if (it.isEmpty()) {
                    api.getAll()
                        .flatMap { apiList ->
                            db.saveAll(apiList).subscribe()
                            Flowable.just(apiList)
                        }
                } else {
                    Flowable.just(it)
                }
            }
    }

    override fun getAll(query: DataSource.Query<Entity>): Flowable<List<Entity>> {

        return when {
            (query.has(CUR_ID) && query.has(START_DATE) && query.has(END_DATE)) -> {
                db.getAll(query).flatMap {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val allDays = TimeUnit.DAYS.convert(
                        dateFormat.parse(query.get(END_DATE)).time -
                                dateFormat.parse(query.get(START_DATE)).time,
                        TimeUnit.MILLISECONDS
                    ) + 1
                    if (it.isEmpty() || it.size < allDays) {
                        api.getAll(query)
                            .flatMap { apiList ->
                                db.saveAll(apiList).subscribe()
                                Flowable.just(apiList)
                            }
                    } else {
                        Flowable.just(it)
                    }
                }
            }
            (query.has(Repository.CUR_CONVERTER) || query.has(Repository.CUR_FAVORITE)) -> {
                db.getAll(query)
                    .flatMap {
                        if (it.isEmpty()) {
                            api.getAll()
                                .flatMap { apiList ->
                                    db.saveAll(apiList).subscribe()
                                    Flowable.just(apiList)
                                }
                                .switchMap { api.getAll(query) }
                                .flatMap { startingList ->
                                    db.saveAll(startingList).subscribe()
                                    Flowable.just(startingList)
                                }
                        } else {
                            Flowable.just(it)
                        }
                    }
            }
            (query.has(DIALOG_CUR)) -> {
                db.getAll(query)
            }
            (query.has(UPDATE_DATAS)) -> {
                api.getAll()
                    .flatMap { apiList ->
                        db.saveAll(apiList).subscribe()
                        Flowable.just(apiList)
                    }
            }
            else -> {
                db.getAll(query)
                    .flatMap {
                        if (it.isEmpty()) {
                            api.getAll(query)
                                .flatMap { apiList ->
                                    db.saveAll(apiList).subscribe()
                                    Flowable.just(apiList)
                                }
                        } else {
                            Flowable.just(it)
                        }
                    }
            }
        }
    }

    override fun get(query: DataSource.Query<Entity>): Observable<Entity> {

        return when {
            (query.has(CUR_ABBREVIATION) && query.has(CUR_DATE) && query.has(DATE_IN_MILLI)) -> {
                db.get(query)
                    .onErrorResumeNext { throwable: Throwable ->
                        api.get(query)
                            .switchMap {
                                if (it is Currency) {
                                    val dbRate = RoomData.of<Rate>(Rate::class)
                                    dbRate.save(
                                        Rate(
                                            rateId = it.curAbbreviation + "_" + query.get(CUR_DATE),
                                            curId = it.curId,
                                            curAbbreviation = it.curAbbreviation,
                                            scale = it.scale,
                                            curOfficialRate = it.curOfficialRate
                                        )
                                    ).subscribe()
                                }
                                Observable.just(it)
                            }
                    }
            }
            else -> {
                db.get(query)
                    .onErrorResumeNext { throwable: Throwable ->
                        api.get(query)
                            .switchMap {
                                db.save(it).subscribe()
                                Observable.just(it)
                            }
                    }
            }
        }


    }

    override fun saveAll(list: List<Entity>): Completable {
        return Completable.defer {
            db.saveAll(list).subscribeOn(Schedulers.io())
        }
    }

    override fun save(item: Entity): Completable {
        return Completable.defer {
            db.save(item).subscribeOn(Schedulers.io())
        }
    }

    override fun delete(item: Entity): Completable {
        return Completable.defer {
            db.delete(item).subscribeOn(Schedulers.io())
        }
    }
}