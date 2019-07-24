package com.bubllbub.exchangerates.models.retrofit

import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.CurrencyFavorite
import com.bubllbub.exchangerates.objects.Ingot
import kotlin.reflect.KClass

object NbrbApiData {
    fun <Entity : Any> of(clazz: KClass<*>): DataSource<Entity> {
        return when (clazz) {
            Currency::class -> CurrencyApiData()
            CurrencyFavorite::class -> CurrencyFavoriteApiData()
            Ingot::class -> IngotApiData()
            else -> throw IllegalArgumentException("Unsupported data type")
        } as DataSource<Entity>
    }
}