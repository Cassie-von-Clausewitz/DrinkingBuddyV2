package com.kyleriedemann.drinkingbuddy.data.source.local

import androidx.room.*
import com.kyleriedemann.drinkingbuddy.data.models.Reading

@Dao
interface ReadingDao {
    /**
     * Select all readings from the table, ordered by datetime
     *
     * @return all readings
     */
    @Query("SELECT * FROM Readings ORDER BY datetime(datetime) DESC")
    suspend fun getReadings(): List<Reading>

    /**
     * Select a reading by its ID
     *
     * @param readingId the id of the reading
     * @return the reading with the readingId, if present
     */
    @Query("SELECT * FROM Readings WHERE id = :readingId")
    suspend fun getReadingById(readingId: String): Reading?

    /**
     * Insert a reading in the database. If the reading exists, replace it.
     *
     * @param reading the reading to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: Reading)

    /**
     * Update a reading
     *
     * @param reading reading to be updated
     * @return the number of readings updated. Will always be 1 in this case
     */
    @Update
    suspend fun updateReading(reading: Reading)

    /**
     * Delete a reading
     *
     * @param reading the reading to be deleted
     * @return number of rows updated. Will always be 1
     */
    @Delete
    suspend fun deleteReading(reading: Reading)

    /**
     * Deletes a reading by ID
     *
     * @param readingId the id of the reading to be deleted
     * @return number of rows updated. Will always be 1
     */
    @Query("DELETE FROM Readings WHERE id = :readingId")
    suspend fun deleteReadingById(readingId: String)

    /**
     * Delete all readings.
     */
    @Query("DELETE FROM Readings")
    suspend fun clearReadings()
}