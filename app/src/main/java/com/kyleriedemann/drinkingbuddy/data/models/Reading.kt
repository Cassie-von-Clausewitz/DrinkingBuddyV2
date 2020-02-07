package com.kyleriedemann.drinkingbuddy.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.*
import kotlin.math.absoluteValue

/**
 * A reading from the device
 */
@Entity(tableName = "readings")
data class Reading @JvmOverloads constructor(
    @ColumnInfo(name = "prediction") var prediction: Float = 0.0f,
    @ColumnInfo(name = "result") var result: Float = 0.0f,
    @ColumnInfo(name = "datetime") var time: Instant = Instant.now(),
    @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
)

fun Reading.diff() = (this.prediction - this.result).absoluteValue