package com.kyleriedemann.drinkingbuddy.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import java.time.Instant
import java.util.*

/**
 * An application log
 */
@Parcelize
@Entity(tableName = "log")
@TypeParceler<Instant, InstantClassParceler>
data class Log @JvmOverloads constructor(
    @ColumnInfo(name = "level") var level: LogLevel,
    @ColumnInfo(name = "tag") var tag: String,
    @ColumnInfo(name = "message") var message: String,
    @ColumnInfo(name = "error") var error: String? = "",
    @ColumnInfo(name = "datetime") var time: Instant = Instant.now(),
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString()
): Parcelable

data class LogTag(
    @ColumnInfo(name = "tag") var tag: String? = "",
    @Ignore var selected: Boolean = false
)

sealed class LogLevel(val level: Int, val name: String): Parcelable {
    @Parcelize object Verbose: LogLevel(2, "Verbose"), Parcelable
    @Parcelize object Debug: LogLevel(3, "Debug"), Parcelable
    @Parcelize object Info: LogLevel(4, "Info"), Parcelable
    @Parcelize object Warn: LogLevel(5, "Warn"), Parcelable
    @Parcelize object Error: LogLevel(6, "Error"), Parcelable
    @Parcelize object Assert: LogLevel(7, "Wtf"), Parcelable
    @Parcelize object Unknown: LogLevel(-1, "Unknown"), Parcelable

    override fun toString(): String = when(this) {
        Verbose -> "LogLevel.Verbose"
        Debug -> "LogLevel.Debug"
        Info -> "LogLevel.Info"
        Warn -> "LogLevel.Warn"
        Error -> "LogLevel.Error"
        Assert -> "LogLevel.Assert"
        Unknown -> "LogLevel.Unknown"
    }

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