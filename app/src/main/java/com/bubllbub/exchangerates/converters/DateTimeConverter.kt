package com.bubllbub.exchangerates.converters

import com.google.gson.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.lang.reflect.Type

class DateTimeConverter : JsonDeserializer<DateTime>, JsonSerializer<DateTime> {
    override fun serialize(
        src: DateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").print(src))
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): DateTime {
        return DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").parseDateTime(json?.asString)
    }
}