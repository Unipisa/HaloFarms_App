package it.unipi.halofarms.data.point

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

/**
 * PointValue's entity
 */
@Parcelize
@Entity(tableName = "pointsValue",  primaryKeys = ["latitude", "longitude", "zoneName" ])
data class PointValue(
    @ColumnInfo(name = "latitude")
    var latitude: Double,
    @ColumnInfo(name = "longitude")
    var longitude: Double,
    @ColumnInfo(name = "zoneName")
    var zoneName: String,
    @ColumnInfo(name = "qrCode")
    var qrCode: String,
) : Parcelable {
    constructor(): this(0.0, 0.0, "null", "null")
}
