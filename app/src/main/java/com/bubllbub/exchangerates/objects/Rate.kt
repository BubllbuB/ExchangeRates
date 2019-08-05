package com.bubllbub.exchangerates.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bubllbub.exchangerates.models.room.RoomDateConverter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

@Entity(tableName = "rates")
@TypeConverters(RoomDateConverter::class)
data class Rate(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var rateId: String = "",
    @SerializedName("Cur_ID")
    @Expose
    var curId: Int = 0,
    @SerializedName("Cur_Abbreviation")
    @Expose
    var curAbbreviation: String = "",
    @SerializedName("Date")
    @Expose
    var date: DateTime = DateTime().withTimeAtStartOfDay(),
    @SerializedName("Cur_Scale")
    @Expose
    var scale: Int = 0,
    @SerializedName("Cur_OfficialRate")
    @Expose
    var curOfficialRate: Double = 0.0
)