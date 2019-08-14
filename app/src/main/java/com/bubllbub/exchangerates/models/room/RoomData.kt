package com.bubllbub.exchangerates.models.room

import androidx.sqlite.db.SimpleSQLiteQuery
import com.bubllbub.exchangerates.models.*

object RoomData {
    fun sqlWhere(table: String, params: Map<String, String>): SimpleSQLiteQuery {
        var query: String
        if (params.containsKey(CUR_ABBREVIATION) && params.containsKey(CUR_DATE) && table == "currency") {
            query =
                "SELECT $table.curId, $table.curAbbreviation, $table.curName, $table.curNameBel, $table.curNameEng, $table.curQuotName, $table.curQuotNameBel, $table.curQuotNameEng, $table.date, rates.curOfficialRate, $table.curPeriodicity, $table.curDateStart, $table.curDateEnd, $table.parentId, $table.scale, $table.symbol FROM $table join rates on currency.curId = rates.curId WHERE rates.curAbbreviation='${params[CUR_ABBREVIATION]}' AND rates.date='${params[DATE_IN_MILLI]}'"
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