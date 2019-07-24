package com.bubllbub.exchangerates.models

import com.bubllbub.exchangerates.App.Companion.isNetworkAvailable
import com.bubllbub.exchangerates.models.retrofit.NbrbApiData
import com.bubllbub.exchangerates.models.room.RoomData
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

object Repository {

    const val CUR_ABBREVIATION = "Cur_Abbreviation"
    const val CUR_NAME = "Cur_Name"
    const val ON_DATE = "onDate"
    const val START_DATE = "startDate"
    const val END_DATE = "endDate"
    const val CUR_ID = "curId"
    const val CUR_DATE = "date"

    inline fun <reified Entity : Any> of(): Repo<Entity> {
        return Repo(NbrbApiData.of(Entity::class), RoomData.of(Entity::class))
    }
}

class Repo<Entity : Any>(val api: DataSource<Entity>, val db: DataSource<Entity>) :
    DataSource<Entity> {
    override fun getAll(): Flowable<List<Entity>> {
        return Flowable.concatArrayEager(
            db.getAll().subscribeOn(Schedulers.io()),
            Flowable.defer {
                if (isNetworkAvailable()) {
                    api.getAll().subscribeOn(Schedulers.io())
                } else {
                    Flowable.empty()
                }
            }.subscribeOn(Schedulers.io())
        )
    }

    override fun getAll(query: DataSource.Query<Entity>): Flowable<List<Entity>> {
        return Flowable.concatArrayEager(
            db.getAll(query).subscribeOn(Schedulers.io()),
            Flowable.defer {
                if (isNetworkAvailable()) {
                    api.getAll(query).subscribeOn(Schedulers.io())
                } else {
                    Flowable.empty()
                }
            }.subscribeOn(Schedulers.io())
        )
    }

    override fun get(query: DataSource.Query<Entity>): Observable<Entity> {
        return Observable.concatArrayEager(
            db.get(query).subscribeOn(Schedulers.io()),
            Observable.defer {
                if (isNetworkAvailable()) {
                    api.get(query).subscribeOn(Schedulers.io())
                } else {
                    Observable.empty()
                }
            }.subscribeOn(Schedulers.io())
        )
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