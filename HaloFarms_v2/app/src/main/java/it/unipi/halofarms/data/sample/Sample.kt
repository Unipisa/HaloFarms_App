package it.unipi.halofarms.data.sample

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

/**
 * Sample's entity
 */
@Parcelize
@Entity(
    tableName = "samples",
    primaryKeys = ["latitude", "longitude", "date"]
)
data class Sample(
    @ColumnInfo(name = "latitude")
    var latitude: Double,
    @ColumnInfo(name = "longitude")
    var longitude: Double,
    @ColumnInfo(name = "sar")
    var sar: Double,
    @ColumnInfo(name = "ph")
    var ph: Double,
    @ColumnInfo(name = "ec")
    var ec: Double,
    @ColumnInfo(name = "cec")
    var cec: Double,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "toBeAnalyzed")
    var toBeAnalyzed: Boolean,
    @ColumnInfo(name = "zoneName")
    var zoneName: String,
) : Parcelable {
    constructor() : this((-1.0), (-1.0), (-1.0), (-1.0), (-1.0), (-1.0), "null", false, "null")
}