package com.kyleriedemann.drinkingbuddy.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kyleriedemann.drinkingbuddy.data.models.Log
import com.kyleriedemann.drinkingbuddy.data.models.LogLevel
import com.kyleriedemann.drinkingbuddy.data.models.LogTag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface LogDao {
    /**
     * Select all logs from the table ordered by datetime
     */
    @Query("SELECT * FROM Log ORDER BY datetime(datetime) DESC")
    fun getLogs(): Flow<List<Log>>

    /**
     * Select a single log by id
     */
    @Query("SELECT * FROM Log WHERE id = :logId")
    suspend fun getLogById(logId: String): Log?

    @Query("SELECT DISTINCT level FROM log ORDER BY level ASC")
    fun getLogLevels(): Flow<List<LogLevel>>

    @Query("SELECT DISTINCT tag FROM log ORDER BY datetime(datetime) DESC")
    fun getLogTags(): Flow<List<LogTag>>

    /**
     * Select all logs from the table with a given log tag ordered by datetime
     */
    @Query("SELECT * FROM Log WHERE tag = :logTag ORDER BY datetime(datetime) DESC")
    fun getLogsByTag(logTag: String): Flow<List<Log>>

    @ExperimentalCoroutinesApi
    fun getLogsByTagDistinctUntilChanged(logTag: String) = getLogsByTag(logTag).distinctUntilChanged()

    /**
     * Insert a log into the table
     *
     * @param log the log to be saved
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: Log)

    /**
     * Update a log in the table
     *
     * @param log the log to be updated
     */
    @Update
    suspend fun updateLog(log: Log)

    /**
     * Delete a log
     *
     * @param log the log that will be deleted
     */
    @Delete
    suspend fun deleteLog(log: Log)

    /**
     * Deletes a log by id
     *
     * @param logId the id of the log that will be deleted if exists
     */
    @Query("DELETE FROM Log WHERE id = :logId")
    suspend fun deleteLogById(logId: String)

    /**
     * Delete all logs.
     */
    @Query("DELETE FROM Log")
    suspend fun clearLogs()
}