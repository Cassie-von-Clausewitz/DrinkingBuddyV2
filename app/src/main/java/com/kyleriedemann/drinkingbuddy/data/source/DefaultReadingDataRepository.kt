package com.kyleriedemann.drinkingbuddy.data.source

import com.kyleriedemann.drinkingbuddy.data.models.Reading
import javax.inject.Inject

class DefaultReadingDataRepository @Inject constructor(private val readingDataSource: ReadingDataSource): ReadingRepository {
    override suspend fun getReadings() = readingDataSource.getReadings()

    override suspend fun getReadingById(readingId: String) = readingDataSource.getReadingById(readingId)

    override suspend fun insertReading(reading: Reading) = readingDataSource.insertReading(reading)

    override suspend fun updateReading(reading: Reading) = readingDataSource.updateReading(reading)

    override suspend fun deleteReading(reading: Reading) = readingDataSource.deleteReading(reading)

    override suspend fun deleteReadingById(readingId: String) = readingDataSource.deleteReadingById(readingId)

    override suspend fun clearReadings() = readingDataSource.clearReadings()
}