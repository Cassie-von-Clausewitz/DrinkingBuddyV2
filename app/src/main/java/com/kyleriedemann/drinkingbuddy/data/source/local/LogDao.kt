package com.kyleriedemann.drinkingbuddy.data.source.local

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
     *
     * @return all logs ordered by datetime descending
     */
    @Query("SELECT * FROM Log ORDER BY datetime(datetime) DESC")
    fun getLogs(): Flow<List<Log>>

    /**
     * Select a single log by id
     *
     * @param logId the id to be queried for
     * @return a log with a given id or null
     */
    @Query("SELECT * FROM Log WHERE id = :logId")
    suspend fun getLogById(logId: String): Log?

    /**
     * Select log levels from the database
     *
     * @return all log levels
     */
    @Query("SELECT DISTINCT level FROM log ORDER BY level ASC")
    fun getLogLevels(): Flow<List<LogLevel>>

    /**
     * Select all logs for a given level
     *
     * @param logLevel level the logs should be filtered by
     * @return all logs for a given log level
     */
    @Query("SELECT * FROM Log WHERE level = :logLevel ORDER BY datetime(datetime) DESC")
    fun getLogsByLevel(logLevel: Int): Flow<List<Log>>

    /**
     * Select all logs for a set of levels
     *
     * @param levels all levels to be included
     * @return all logs of a level included in the filter
     */
    @Query("SELECT * FROM Log WHERE level IN(:levels) ORDER BY datetime(datetime) DESC")
    fun getLogsFilteredByLevel(levels: IntArray): Flow<List<Log>>

    @ExperimentalCoroutinesApi
    fun getLogsByLevelDistinctUntilChanged(logLevel: LogLevel) = getLogsByLevel(logLevel.level).distinctUntilChanged()

    /**
     * Select all log tags
     *
     * @return all log tags
     */
    @Query("SELECT DISTINCT tag FROM log ORDER BY datetime(datetime) DESC")
    fun getLogTags(): Flow<List<LogTag>>

    /**
     * Select all logs from the table with a given log tag ordered by datetime
     *
     * @param logTags a set of log tags to be included
     * @return all logs with a tag in the set of included tags
     */
    @Query("SELECT * FROM Log WHERE tag IN(:logTags) ORDER BY datetime(datetime) DESC")
    fun getLogsFilteredByTag(logTags: Array<String>): Flow<List<Log>>

    /**
     * Select all logs of a given set of levels and log tags
     *
     * @param levels all levels to be included
     * @param logTags all log tags to be included
     * @return all tags of a set of levels and log tags
     */
    @Query("SELECT * FROM Log WHERE level IN(:levels) AND tag IN(:logTags) ORDER BY datetime(datetime) DESC")
    fun getLogsFilteredByLevelAndTag(levels: IntArray, logTags: Array<String>): Flow<List<Log>>

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