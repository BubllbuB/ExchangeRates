package com.bubllbub.exchangerates.objects

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencyFavorites")
data class CurrencyFavorite(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val favCurrId: Int = 0,
    @Embedded
    val currency: Currency = Currency()
)