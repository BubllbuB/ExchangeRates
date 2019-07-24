package com.bubllbub.exchangerates.models.room

import androidx.sqlite.db.SimpleSQLiteQuery
import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.CurrencyFavorite
import com.bubllbub.exchangerates.objects.Ingot
import kotlin.reflect.KClass

object RoomData {

    private val db: AppDatabase by lazy { AppDatabase.getDatabase(App.appContext()) }

    fun <Entity : Any> of(clazz: KClass<*>): DataSource<Entity> {
        return when (clazz) {
            Currency::class -> CurrencyRoomData(db.currencyDao())
            CurrencyFavorite::class -> CurrencyFavoriteRoomData(db.currencyFavoritesDao())
            Ingot::class -> IngotRoomData(db.ingotDao())
            else -> throw IllegalArgumentException("Unsupported data type")
        } as DataSource<Entity>
    }

    fun clearDb() {
        db.clearAllTables()
    }

    // util method for converting PARAMS MAP to sql QUERY with WHERE keyword
    fun sqlWhere(table: String, params: Map<String, String>): SimpleSQLiteQuery {
        var query = "SELECT * FROM $table"
        params.keys.forEachIndexed { i, s ->
            query += if (i == 0) " WHERE" else " AND"
            query += " $s = ?"
        }

        val args = params.values.toTypedArray()
        return SimpleSQLiteQuery(query, args)
    }
}