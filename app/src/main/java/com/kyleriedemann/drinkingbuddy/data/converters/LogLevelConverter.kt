package com.kyleriedemann.drinkingbuddy.data.converters

import androidx.room.TypeConverter
import com.kyleriedemann.drinkingbuddy.data.models.LogLevel

object LogLevelConverter {
    @JvmStatic @TypeConverter
    fun fromInt(level: Int): LogLevel = LogLevel.fromLevel(level)

    @JvmStatic @TypeConverter
    fun fromLogLevel(logLevel: LogLevel): Int = logLevel.level
}