package it.unipi.halofarms.data.map

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

/**
 * Map entity
 */
@Parcelize
@Entity(tableName = "maps", primaryKeys = ["name"])
data class Map(
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "latitude")
    var latitude: Double,
    @ColumnInfo(name = "longitude")
    var longitude: Double,
    @ColumnInfo(name = "mode")
    var mode: String,
    @ColumnInfo(name = "area")
    var area: Double,
    @ColumnInfo(name = "done")
    var done: Boolean,
    @ColumnInfo(name = "date")
    var date: String,
) : Parcelable {
    constructor() : this("null", 0.0, 0.0, "null", 0.0, false, "null")
}