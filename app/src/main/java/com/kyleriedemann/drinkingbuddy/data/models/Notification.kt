package com.kyleriedemann.drinkingbuddy.data.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.*
import java.time.Instant
import java.util.*

/**
 * A notification shown to the user
 */
@Parcelize
@TypeParceler<Instant, InstantClassParceler>
@Entity(tableName = "notifications")
data class Notification @JvmOverloads constructor(
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "message") var message: String = "",
    @ColumnInfo(name = "read") var read: Boolean = false,
    @ColumnInfo(name = "datetime") var time: Instant = Instant.now(),
    @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString()
): Parcelable

object InstantClassParceler: Parceler<Instant> {
    override fun create(parcel: Parcel): Instant = Instant.ofEpochMilli(parcel.readLong())

    override fun Instant.write(parcel: Parcel, flags: Int) {
        parcel.writeLong(this.toEpochMilli())
    }
}