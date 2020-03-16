@file:JvmName("Converters")
package com.kyleriedemann.drinkingbuddy.ui.common

import com.kyleriedemann.drinkingbuddy.R
import com.kyleriedemann.drinkingbuddy.data.models.LogLevel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun dateToString(value: Instant): String {
    return DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.LONG)
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())
        .format(value)
}

fun floatToString(value: Float): String = value.toString()

fun logLevelToColor(level: LogLevel): Int = when(level) {
    LogLevel.Verbose -> R.color.log_level_verbose
    LogLevel.Debug -> R.color.log_level_debug
    LogLevel.Info -> R.color.log_level_info
    LogLevel.Warn -> R.color.log_level_warn
    LogLevel.Error -> R.color.log_level_error
    LogLevel.Assert -> R.color.log_level_assert
    LogLevel.Unknown -> R.color.log_level_unknown
}