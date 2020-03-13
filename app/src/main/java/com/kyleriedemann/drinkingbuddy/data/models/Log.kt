package com.kyleriedemann.drinkingbuddy.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.*

/**
 * An application log
 */
@Entity(tableName = "log")
data class Log @JvmOverloads constructor(
    @ColumnInfo(name = "level") var level: LogLevel,
    @ColumnInfo(name = "tag") var tag: String,
    @ColumnInfo(name = "message") var message: String,
    @ColumnInfo(name = "error") var error: String? = "",
    @ColumnInfo(name = "datetime") var time: Instant = Instant.now(),
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString()
)

data class LogTag(
    @ColumnInfo(name = "tag") var tag: String? = ""
)

sealed class LogLevel(val level: Int, val name: String) {
    object Verbose: LogLevel(2, "Verbose")
    object Debug: LogLevel(3, "Debug")
    object Info: LogLevel(4, "Info")
    object Warn: LogLevel(5, "Warn")
    object Error: LogLevel(6, "Error")
    object Assert: LogLevel(7, "Wtf")
    object Unknown: LogLevel(-1, "Unknown")

    companion object {
        fun fromLevel(level: Int): LogLevel = when(level) {
            Verbose.level -> Verbose
            Debug.level -> Debug
            Info.level -> Info
            Warn.level -> Warn
            Error.level -> Error
            Assert.level -> Assert
            Unknown.level -> Unknown
            else -> Unknown
        }

        fun fromName(name: String): LogLevel = when(name) {
            Verbose.name -> Verbose
            Debug.name -> Debug
            Info.name -> Info
            Warn.name -> Warn
            Error.name -> Error
            Assert.name -> Assert
            Unknown.name -> Unknown
            else -> Unknown
        }
    }
}