package com.kyleriedemann.drinkingbuddy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kyleriedemann.drinkingbuddy.data.converters.InstantConverter
import com.kyleriedemann.drinkingbuddy.data.converters.LogLevelConverter
import com.kyleriedemann.drinkingbuddy.data.models.Log
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import com.kyleriedemann.drinkingbuddy.data.models.Reading
import com.kyleriedemann.drinkingbuddy.data.source.local.LogDao
import com.kyleriedemann.drinkingbuddy.data.source.local.NotificationDao
import com.kyleriedemann.drinkingbuddy.data.source.local.ReadingDao

@TypeConverters(InstantConverter::class, LogLevelConverter::class)
@Database(entities = [Reading::class, Notification::class, Log::class], version = 3, exportSchema = false)
abstract class DrinkingBuddyDb : RoomDatabase() {

    abstract fun readingDao() : ReadingDao

    abstract fun notificationDao(): NotificationDao

    abstract fun logDao(): LogDao
}