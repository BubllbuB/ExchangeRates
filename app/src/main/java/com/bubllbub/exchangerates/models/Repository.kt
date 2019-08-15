package com.bubllbub.exchangerates.models


import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.di.DaggerAppComponent
import com.bubllbub.exchangerates.di.modules.AppModule
import com.bubllbub.exchangerates.di.modules.RoomModule
import com.bubllbub.exchangerates.models.room.roomDatas.RateRoomData
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Rate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.reflect.KClass


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

class Repo<Entity : Any>(val api: DataSource<Entity>, private val db: DataSource<Entity>, currentKClass: KClass<Entity>) :
    DataSource<Entity> {

    @Inject
    lateinit var rateRoomData: RateRoomData

    init {
        if(Currency::class == currentKClass) {
            this as Repo<Currency>

            DaggerAppComponent.builder()
                .appModule(AppModule(App.instance))
                .roomModule(RoomModule())
                .build()
                .inject(this)
        }
    }

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
            (query.has(CUR_CONVERTER) || query.has(CUR_FAVORITE)) -> {
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
                                    rateRoomData.save(
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