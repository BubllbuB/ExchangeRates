package com.bubllbub.exchangerates.models.room

import androidx.sqlite.db.SimpleSQLiteQuery
import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.models.DataSource
import com.bubllbub.exchangerates.models.Repository.CUR_ABBREVIATION
import com.bubllbub.exchangerates.models.Repository.CUR_DATE
import com.bubllbub.exchangerates.models.Repository.CUR_ID
import com.bubllbub.exchangerates.models.Repository.DATE_IN_MILLI
import com.bubllbub.exchangerates.models.Repository.DIALOG_CUR
import com.bubllbub.exchangerates.models.Repository.END_DATE
import com.bubllbub.exchangerates.models.Repository.RATE_ID
import com.bubllbub.exchangerates.models.Repository.START_DATE
import com.bubllbub.exchangerates.models.room.roomDatas.*
import com.bubllbub.exchangerates.objects.*
import kotlin.reflect.KClass

object RoomData {

    private val db: AppDatabase by lazy { AppDatabase.getDatabase(App.appContext()) }

    fun <Entity : Any> of(clazz: KClass<*>): DataSource<Entity> {
        return when (clazz) {
            Currency::class -> CurrencyRoomData(db.currencyDao())
            Ingot::class -> IngotRoomData(db.ingotDao())
            Rate::class -> RateRoomData(db.rateDao())
            else -> throw IllegalArgumentException("Unsupported data type")
        } as DataSource<Entity>
    }

    fun clearDb() {
        db.clearAllTables()
    }


    fun sqlWhere(table: String, params: Map<String, String>): SimpleSQLiteQuery {
        var query: String
        if (params.containsKey(CUR_ABBREVIATION) && params.containsKey(CUR_DATE) && table == "currency") {
            query =
                "SELECT $table.curId, $table.curAbbreviation, $table.curName, $table.curNameBel, $table.curNameEng, $table.date, rates.curOfficialRate, $table.curPeriodicity, $table.curDateStart, $table.curDateEnd, $table.parentId, $table.scale, $table.symbol FROM $table join rates on currency.curId = rates.curId WHERE rates.curAbbreviation='${params[CUR_ABBREVIATION]}' AND rates.date='${params[DATE_IN_MILLI]}'"
        } else {
            query = "SELECT * FROM $table"
            val cleanParams = cleanParams(params)
            cleanParams.keys.forEachIndexed { i, s ->
                query += if (i == 0) " WHERE" else " AND"
                query += " $s = '${cleanParams[s]}'"
            }
        }
        return SimpleSQLiteQuery(query)
    }

    fun sqlNotIn(table: String, params: Map<String, String>): SimpleSQLiteQuery {
        var query = "SELECT * FROM $table"
        val cleanParams = cleanParams(params)
        cleanParams.keys.forEachIndexed { i, s ->
            query += if (i == 0) " WHERE" else " AND"
            query += " $s NOT IN (${cleanParams[s]})"
        }
        return SimpleSQLiteQuery(query)
    }

    fun sqlBetween(table: String, params: Map<String, String>): SimpleSQLiteQuery {
        var query = "SELECT * FROM $table"
        if (params.containsKey(CUR_ID) && params.containsKey(START_DATE) && params.containsKey(
                END_DATE
            ) && params.containsKey(CUR_ABBREVIATION)
        ) {
            query += " WHERE $RATE_ID BETWEEN ('${params[CUR_ABBREVIATION]}_${params[START_DATE]}') AND ('${params[CUR_ABBREVIATION]}_${params[END_DATE]}')"
        }
        return SimpleSQLiteQuery(query)
    }

    private fun cleanParams(params: Map<String, String>): Map<String, String> {
        val cleanParams = params.toMutableMap()
        cleanParams.remove(DIALOG_CUR)
        return cleanParams
    }
}