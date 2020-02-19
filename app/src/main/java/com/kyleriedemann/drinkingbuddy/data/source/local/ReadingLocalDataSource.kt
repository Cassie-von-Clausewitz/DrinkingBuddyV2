package com.kyleriedemann.drinkingbuddy.data.source.local

import com.kyleriedemann.drinkingbuddy.data.models.Reading
import com.kyleriedemann.drinkingbuddy.data.source.ReadingDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.kyleriedemann.drinkingbuddy.data.LceState.Success
import com.kyleriedemann.drinkingbuddy.data.LceState.Error

class ReadingLocalDataSource internal constructor(
    private val readingDao: ReadingDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ReadingDataSource {

    override suspend fun getReadings() = withContext(ioDispatcher) {
        return@withContext try {
            Success(readingDao.getReadings())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getReadingById(readingId: String) = withContext(ioDispatcher) {
        try {
            val reading = readingDao.getReadingById(readingId)
            if (reading != null) {
                return@withContext Success(reading)
            } else {
                return@withContext Error(Exception("Reading not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun insertReading(reading: Reading) = withContext(ioDispatcher) {
        readingDao.insertReading(reading)
    }

    override suspend fun updateReading(reading: Reading) = withContext(ioDispatcher) {
        readingDao.updateReading(reading)
    }

    override suspend fun deleteReading(reading: Reading) = withContext(ioDispatcher) {
        readingDao.deleteReading(reading)
    }

    override suspend fun deleteReadingById(readingId: String) = withContext(ioDispatcher) {
        readingDao.deleteReadingById(readingId)
    }

    override suspend fun clearReadings() = withContext(ioDispatcher) {
        readingDao.clearReadings()
    }
}