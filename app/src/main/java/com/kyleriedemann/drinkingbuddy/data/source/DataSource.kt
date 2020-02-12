package com.kyleriedemann.drinkingbuddy.data.source

import androidx.lifecycle.LiveData
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import com.kyleriedemann.drinkingbuddy.data.models.Reading
import com.kyleriedemann.drinkingbuddy.data.Result

interface NotificationDataSource {
    suspend fun getNotifications(): Result<List<Notification>>

    fun getNotificationsLive(): LiveData<List<Notification>>

    suspend fun getNotificationById(notificationId: String): Result<Notification>

    suspend fun insertNotification(notification: Notification)

    suspend fun updateNotification(notification: Notification)

    suspend fun markRead(notificationId: String, read: Boolean = true)

    suspend fun deleteNotification(notification: Notification)

    suspend fun deleteNotificationById(notificationId: String)

    suspend fun clearNotifications()
}

interface ReadingDataSource {
    suspend fun getReadings(): Result<List<Reading>>

    suspend fun getReadingById(readingId: String): Result<Reading>

    suspend fun insertReading(reading: Reading)

    suspend fun updateReading(reading: Reading)

    suspend fun deleteReading(reading: Reading)

    suspend fun deleteReadingById(readingId: String)

    suspend fun clearReadings()
}