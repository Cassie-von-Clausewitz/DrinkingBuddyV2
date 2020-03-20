package com.kyleriedemann.drinkingbuddy.data.source.local

import com.kyleriedemann.drinkingbuddy.data.models.Log
import com.kyleriedemann.drinkingbuddy.ui.log.FilterRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Local data source for [Log]s. Logs are filtered with a global [FilterRepository] that returns
 * device local settings for logs to be included in the standard log presentation.
 *
 * @param logDao [Log] DAO class that handles writing and reading from the local db
 * @param filterRepository repository of local user settings for filtering log output
 * @param dispatcher dispatcher on which to run all local coroutines
 */
class LogLocalDataSource @Inject constructor(
    private val logDao: LogDao,
    private val filterRepository: FilterRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun filteredLogs() = withContext(dispatcher) {
        filterRepository.filter.flatMapLatest {
            if (it.levelFilter.isNotEmpty && it.tagFilter.isEmpty) {
                logDao.getLogsFilteredByLevel(it.levelFilter.intArray)
            } else if (it.levelFilter.isEmpty && it.tagFilter.isNotEmpty) {
                logDao.getLogsFilteredByTag(it.tagFilter.array)
            } else if (it.levelFilter.isNotEmpty && it.tagFilter.isNotEmpty) {
                logDao.getLogsFilteredByLevelAndTag(it.levelFilter.intArray, it.tagFilter.array)
            } else {
                logDao.getLogs()
            }
        }
    }

    fun levels() = logDao.getLogLevels().flowOn(dispatcher)

    fun tags() = logDao.getLogTags().flowOn(dispatcher)

    suspend fun save(log: Log) = withContext(dispatcher) {
        val record = logDao.getLogById(log.id)
        if (record != null) {
            logDao.updateLog(log)
        } else {
            logDao.insertLog(log)
        }
    }
}