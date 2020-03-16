package com.kyleriedemann.drinkingbuddy.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    /**
     * Select all notifications from the table, ordered by datetime
     *
     * @return all notifications
     */
    @Query("SELECT * FROM Notifications ORDER BY datetime(datetime) DESC")
    suspend fun getNotifications(): List<Notification>

    /**
     * Select live notifications from the table, ordered by datetime
     *
     * @return all notifications
     */
    @Query("SELECT * FROM Notifications ORDER BY datetime(datetime) DESC")
    fun getLiveNotifications(): LiveData<List<Notification>>

    /**
     * Select live count of unread notifications
     */
    @Query("SELECT count(id) from notifications WHERE read = 0")
    fun getLiveUnreadNotificationCount(): LiveData<Int>

    /**
     * Select a flow of the unread count
     */
    @Query("SELECT count(id) from notifications WHERE read = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    /**
     * Select a notification by its ID
     *
     * @param notificationId the id of the notification
     * @return the notification with the notificationId, if present
     */
    @Query("SELECT * FROM Notifications WHERE id = :notificationId")
    suspend fun getNotificationById(notificationId: String): Notification?

    /**
     * Select a notification by its ID
     *
     * @param notificationId the id of the notification
     * @return the notification with the notificationId, if present
     */
    @Query("SELECT * FROM Notifications WHERE id = :notificationId")
    fun getLiveNotificationById(notificationId: String): LiveData<Notification?>

    /**
     * Insert a notification in the database. If the notification exists, replace it.
     *
     * @param notification the notification to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    /**
     * Update a notification
     *
     * @param notification notification to be updated
     */
    @Update
    suspend fun updateNotification(notification: Notification)

    /**
     * Updates the read status of a notification
     *
     * @param notificationId the id of the notification
     * @param read status to be updated
     */
    @Query("UPDATE Notifications SET read = :read WHERE id = :notificationId")
    suspend fun markRead(notificationId: String, read: Boolean = true)

    /**
     * Updates the read status of all notifications
     */
    @Query("UPDATE notifications SET read=:read")
    suspend fun markAllRead(read: Boolean = true)

    /**
     * Delete a notification
     *
     * @param notification the notification to be deleted
     */
    @Delete
    suspend fun deleteNotification(notification: Notification)

    /**
     * Deletes a notification by ID
     *
     * @param notificationId the id of the notification to be deleted
     */
    @Query("DELETE FROM Notifications WHERE id = :notificationId")
    suspend fun deleteNotificationById(notificationId: String)

    /**
     * Delete all notifications.
     */
    @Query("DELETE FROM Notifications")
    suspend fun clearNotifications()
}