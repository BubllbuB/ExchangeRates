package com.bubllbub.exchangerates.models

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

interface DataSource<T : Any> {

    fun getAll(): Flowable<List<T>>

    fun get(query: Query<T>): Observable<T>

    fun getAll(query: Query<T>): Flowable<List<T>>

    fun saveAll(list: List<T>): Completable

    fun save(item: T): Completable

    fun delete(item: T): Completable

    fun query(): Query<T> {
        return Query(this)
    }

    class Query<T : Any> constructor(private val dataSource: DataSource<T>) {

        val params: MutableMap<String, String> = mutableMapOf()

        fun has(property: String): Boolean {
            return params[property] != null
        }

        fun get(property: String): String? {
            return params[property]
        }

        fun where(property: String, value: String): Query<T> {
            params[property] = value
            return this
        }

        fun findAll(): Flowable<List<T>> {
            return dataSource.getAll(this)
        }

        fun findOne(): Observable<T> {
            return dataSource.get(this)
        }
    }
}