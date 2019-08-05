package com.bubllbub.exchangerates.models.retrofit

import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.models.retrofit.apiDatas.*
import com.bubllbub.exchangerates.objects.*
import kotlin.reflect.KClass

object NbrbApiData {
    fun <Entity : Any> of(clazz: KClass<*>): DataSource<Entity> {
        return when (clazz) {
            Currency::class -> CurrencyApiData()
            Ingot::class -> IngotApiData()
            Rate::class -> RateApiData()
            else -> throw IllegalArgumentException("Unsupported data type")
        } as DataSource<Entity>
    }
}