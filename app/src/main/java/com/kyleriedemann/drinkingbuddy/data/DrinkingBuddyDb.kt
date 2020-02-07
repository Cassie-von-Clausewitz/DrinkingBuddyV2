package com.kyleriedemann.drinkingbuddy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kyleriedemann.drinkingbuddy.data.converters.InstantConverter
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import com.kyleriedemann.drinkingbuddy.data.models.Reading
import com.kyleriedemann.drinkingbuddy.data.source.local.NotificationDao
import com.kyleriedemann.drinkingbuddy.data.source.local.ReadingDao

@TypeConverters(InstantConverter::class)
@Database(entities = [Reading::class, Notification::class], version = 1, exportSchema = true)
abstract class DrinkingBuddyDb : RoomDatabase() {

    abstract fun readingDao() : ReadingDao

    abstract fun notificationDao(): NotificationDao
}