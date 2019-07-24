package com.bubllbub.exchangerates.objects

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bubllbub.exchangerates.models.room.RoomDateConverter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity
@TypeConverters(RoomDateConverter::class)
data class Currency(
    @SerializedName("Cur_ID")
    @Expose
    @PrimaryKey
    var curId: Int = 0,
    @SerializedName("Cur_Abbreviation")
    @Expose
    var curAbbreviation: String = "",
    @SerializedName("Cur_Name")
    @Expose
    var curName: String = "",
    @SerializedName("Cur_Name_Bel")
    @Expose
    var curNameBel: String = "",
    @SerializedName("Cur_Name_Eng")
    @Expose
    var curNameEng: String = "",
    @SerializedName("Date")
    @Expose
    var date: Date = Date(),
    @SerializedName("Cur_OfficialRate")
    @Expose
    var curOfficialRate: Double = 0.0,
    @SerializedName("Cur_Periodicity")
    @Expose
    var curPeriodicity: Int = 0,
    @SerializedName("Cur_DateStart")
    @Expose
    var curDateStart: Date = Date(),
    @SerializedName("Cur_DateEnd")
    @Expose
    var curDateEnd: Date = Date(),
    @SerializedName("Cur_ParentID")
    @Expose
    var parentId: Int = 0,
    @SerializedName("Cur_Scale")
    @Expose
    var scale: Int = 0,
    @Ignore
    var symbol: Int = 0,
    @Ignore
    var calcAmount: Double = 0.0
)