package com.bubllbub.exchangerates.models.room

import androidx.room.TypeConverter
import java.util.*


class RoomDateConverter {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return if (dateLong == null) {
            dateLong
        } else {
            Date(dateLong)
        }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return (date?.time)
    }
}