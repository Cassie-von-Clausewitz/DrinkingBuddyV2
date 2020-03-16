package com.kyleriedemann.drinkingbuddy.data.source

import androidx.lifecycle.LiveData
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import com.kyleriedemann.drinkingbuddy.data.models.Reading
import com.kyleriedemann.drinkingbuddy.data.LceState
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    // todo remove the result class and return these as live data, updated from external sources if needed
    suspend fun getNotifications(): LceState<List<Notification>>

    fun getLiveNotifications(): LiveData<List<Notification>>

    fun getLiveUnreadNotificationCount(): LiveData<Int>

    fun getUnreadNotificationCount(): Flow<Int>

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

interface ReadingRepository {
    suspend fun getReadings(): LceState<List<Reading>>

    suspend fun getReadingById(readingId: String): LceState<Reading>

    suspend fun insertReading(reading: Reading)

    suspend fun updateReading(reading: Reading)

    suspend fun deleteReading(reading: Reading)

    suspend fun deleteReadingById(readingId: String)

    suspend fun clearReadings()
}