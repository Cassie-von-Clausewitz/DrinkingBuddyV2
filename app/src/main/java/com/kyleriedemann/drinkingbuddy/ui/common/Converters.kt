@file:JvmName("Converters")
package com.kyleriedemann.drinkingbuddy.ui.common

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