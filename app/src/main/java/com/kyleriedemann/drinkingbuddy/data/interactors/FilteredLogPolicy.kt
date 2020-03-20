package com.kyleriedemann.drinkingbuddy.data.interactors

import com.kyleriedemann.drinkingbuddy.data.source.local.LogDao
import com.kyleriedemann.drinkingbuddy.ui.log.FilterRepository
import com.kyleriedemann.drinkingbuddy.ui.log.LogFilters
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class FilteredLogPolicy @Inject constructor(
    private val logDao: LogDao,
    private val filterRepository: FilterRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke() = withContext(dispatcher) {
        filterRepository.filter.catch {
            logError(it)
            emit(LogFilters())
        }.flatMapLatest {
            if (it.levelFilter.isNotEmpty && it.tagFilter.isEmpty) {
                logDao.getLogsFilteredByLevel(it.levelFilter.intArray)
            } else if (it.levelFilter.isEmpty && it.tagFilter.isNotEmpty) {
                logDao.getLogsFilteredByTag(it.tagFilter.array)
            } else if (it.levelFilter.isNotEmpty && it.tagFilter.isNotEmpty) {
                logDao.getLogsFilteredByLevelAndTag(it.levelFilter.intArray, it.tagFilter.array)
            } else {
                logDao.getLogs()
            }
        }.catch {
            logError(it)
            emit(emptyList())
        }
    }

    private fun logError(t: Throwable) = Timber.e(t, "Error in FilteredLogPolicy")
}