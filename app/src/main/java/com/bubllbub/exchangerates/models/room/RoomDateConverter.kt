package com.bubllbub.exchangerates.models.room

import androidx.room.TypeConverter
import org.joda.time.DateTime
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

    @TypeConverter
    fun toDateTime(dateLong: Long?): DateTime? {
        return if (dateLong == null) {
            dateLong
        } else {
            DateTime().withMillis(dateLong)
        }
    }

    @TypeConverter
    fun fromDateTime(date: DateTime?): Long? {
        return (date?.millis)
    }
}