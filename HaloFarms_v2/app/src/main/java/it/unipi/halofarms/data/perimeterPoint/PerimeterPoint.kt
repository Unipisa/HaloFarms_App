package it.unipi.halofarms.data.perimeterPoint

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

/**
 * Perimeter point entity
 */
@Parcelize
@Entity(tableName = "perimeterPoints",  primaryKeys = ["latitude", "longitude"])
data class PerimeterPoint(
    @ColumnInfo(name = "latitude")
    var latitude: Double,
    @ColumnInfo(name = "longitude")
    var longitude: Double,
    @ColumnInfo(name = "zoneName")
    var zoneName: String
) : Parcelable {
    constructor() : this(0.0, 0.0, "null")
}