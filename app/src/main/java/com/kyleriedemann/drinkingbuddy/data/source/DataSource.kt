package com.kyleriedemann.drinkingbuddy.data.source

import androidx.lifecycle.LiveData
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import com.kyleriedemann.drinkingbuddy.data.models.Reading
import com.kyleriedemann.drinkingbuddy.data.LceState

interface NotificationDataSource {
    suspend fun getNotifications(): LceState<List<Notification>>

    fun getNotificationsLive(): LiveData<List<Notification>>

    suspend fun getNotificationById(notificationId: String): LceState<Notification>

    fun getLiveNotificationById(notificationId: String): LiveData<Notification?>

    suspend fun insertNotification(notification: Notification)

    suspend fun updateNotification(notification: Notification)

    suspend fun markRead(notificationId: String, read: Boolean = true)

    suspend fun markAllRead(read: Boolean = true)

    suspend fun deleteNotification(notification: Notification)

    suspend fun deleteNotificationById(notificationId: String)

    suspend fun clearNotifications()
}

interface ReadingDataSource {
    suspend fun getReadings(): LceState<List<Reading>>

    suspend fun getReadingById(readingId: String): LceState<Reading>

    suspend fun insertReading(reading: Reading)

    suspend fun updateReading(reading: Reading)

    suspend fun deleteReading(reading: Reading)

    suspend fun deleteReadingById(readingId: String)

    suspend fun clearReadings()
}