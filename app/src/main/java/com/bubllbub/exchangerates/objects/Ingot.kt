package com.bubllbub.exchangerates.objects

import androidx.room.*
import com.bubllbub.exchangerates.models.room.RoomDateConverter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "ingots")
@TypeConverters(RoomDateConverter::class)
data class Ingot(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idIngot")
    var id: Int = 0,
    @SerializedName("Id")
    @Expose
    var ingotId: Int = 0,
    @SerializedName("Name")
    @Expose
    var ingotName: String = "",
    @SerializedName("NameBel")
    @Expose
    var ingotNameBel: String = "",
    @SerializedName("NameEng")
    @Expose
    var ingotNameEng: String = "",
    @SerializedName("Date")
    @Expose
    var date: Date = Date(),
    @SerializedName("CertificateRubles")
    @Expose
    var ingotCertificateRubles: Double = 0.0,
    @SerializedName("EntitiesRubles")
    @Expose
    var ingotEntitiesRubles: Double = 0.0,
    @SerializedName("MetalID")
    @Expose
    var metalIdApi: Int = 0,
    @SerializedName("Nominal")
    @Expose
    var nominal: Int = 0,
    var symbol: Int = 0,
    @Ignore
    var rates: List<Ingot> = listOf()
)