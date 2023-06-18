package it.unipi.halofarms.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import it.unipi.halofarms.screen.zone.MapViewModel
import java.util.*
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin



/**
 * Finds the centroid
 *
 * @param points
 * @return LatLng
 */
fun findCentroid(points: List<LatLng?>): LatLng {
    var x = 0.0
    var y = 0.0
    for (p in points) {
        x += p?.latitude!!
        y += p.longitude
    }
    return LatLng(x / points.size, y / points.size)
}

/**
 * Sorts the perimeter points
 *
 * @param points Points to be sorted
 */
fun sortVerticies(points: MutableList<LatLng>) {
    val center: LatLng = findCentroid(points)
    points.sortWith { a, b ->
        val a1 = ((a?.longitude)?.minus(center.longitude)?.let {
            atan2(a.latitude - center.latitude,
                it)
        }?.let { Math.toDegrees(it) }?.plus(360))?.rem(360)
        val a2 = ((b?.latitude)?.minus(center.latitude)?.let {
            atan2(it,
                b.longitude - center.longitude)
        }?.let { Math.toDegrees(it) }?.plus(360))?.rem(360)
        (a2?.let { a1?.minus(it) })?.toInt()!!
    }

}

/**
 * Calculate the areas of internal cells according to field's area.
 * Bigger field implies bigger cells area
 *
 * @return meters Area of the current cell
 */
fun areaOfSquare(area: Float): Float {
    val meters: Float = if (area < 0.1) {
        5f
    } else if (area > 0.1 && area < 0.2) {
        6f
    } else if (area > 0.2f && area < 0.3) {
        7f
    } else if (area > 0.3 && area < 0.5) {
        8f
    } else if (area > 0.5 && area < 0.7) {
        9f
    } else if (area > 0.7 && area < 0.9) {
        9.5f
    } else if (area > 0.9 && area < 2) {
        15f
    } else if (area > 2 && area < 3) {
        20f
    } else if (area > 3 && area < 4) {
        25f
    } else {
        30f
    }
    return meters
}


fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val theta = lon1 - lon2
    var dist = (sin(deg2rad(lat1))
            * sin(deg2rad(lat2))
            + (cos(deg2rad(lat1))
            * cos(deg2rad(lat2))
            * cos(deg2rad(theta))))
    dist = acos(dist)
    dist = rad2deg(dist)
    dist *= 60 * 1.1515
    return (dist * 1.609344)
}

private fun deg2rad(deg: Double): Double {
    return deg * Math.PI / 180.0
}

private fun rad2deg(rad: Double): Double {
    return rad * 180.0 / Math.PI
}

/**
 * Convert a string into a list of LatLng.
 * The string has the format of double.
 * It is saved on Firestore in this way because there is a bug.
 * @param string to convert in a list of LatLng
 * @return the list containing coordinates.
 */
private fun fromStringToLatLng(string: String): ArrayList<LatLng> {
    val tokenizer = StringTokenizer(string)
    val coordinates: ArrayList<LatLng> = ArrayList()
    while (tokenizer.hasMoreTokens()) {
        coordinates.add(LatLng(tokenizer.nextToken().toDouble(),
            tokenizer.nextToken().toDouble()))
    }
    return coordinates
}

/**
 * Convert a Polygon into a String that will be saved to firestore.
 * @param polygon whose points will be saved as string.
 * @return the string containing coordinates.
 */
private fun fromPolygonToString(polygon: Polygon?): String {
    val latLngs: List<LatLng> = polygon!!.points
    val s = StringBuilder()
    for (latLng in latLngs) {
        s.append(latLng.latitude).append(" ").append(latLng.longitude).append(" ")
    }
    return s.toString()
}