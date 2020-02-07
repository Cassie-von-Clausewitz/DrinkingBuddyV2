package com.kyleriedemann.drinkingbuddy.data.converters

import androidx.room.TypeConverter
import java.time.Instant
import java.time.format.DateTimeFormatter

object InstantConverter {
    private val formatter = DateTimeFormatter.ISO_INSTANT

    @JvmStatic
    @TypeConverter
    fun fromTimestamp(value: String?): Instant = Instant.from(formatter.parse(value))

    @JvmStatic
    @TypeConverter
    fun toTimestamp(instant: Instant?): String = instant.toString()
}