package it.unipi.halofarms.util

import com.google.android.gms.maps.model.LatLng
import it.unipi.halofarms.data.map.Map
import it.unipi.halofarms.data.map.MapRepo
import it.unipi.halofarms.data.perimeterPoint.PerimeterPoint
import it.unipi.halofarms.data.perimeterPoint.PerimeterPointRepo
import it.unipi.halofarms.data.point.PointValue
import it.unipi.halofarms.data.point.PointValueRepo
import it.unipi.halofarms.data.sample.Sample
import it.unipi.halofarms.data.sample.SampleRepo
import kotlin.math.atan2



/**
 * Finds the centroid of a coords' list
 *
 * @param points Coords list
 * @return LatLng List's centroid
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
 * Sorts the perimeter points' map
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
 * Change a perimeter point's list into a latlng array
 *
 * @param pPoints Perimeter point's list
 */
fun fromDbToLatLng(pPoints : List<PerimeterPoint>): ArrayList<LatLng> {
    val perimLatLng = arrayListOf<LatLng>()
    for(point in pPoints){
        val latlngAux = LatLng(
            point.latitude,
            point.longitude
        )
        perimLatLng.add(latlngAux)
    }
    return perimLatLng
}

/**
 * Adds the cloud data (maps) to the local database
 *
 * @param mapRepo Map repository (Room)
 * @param zns Map's list from Firestore
 */
fun mapsFromCloud(mapRepo: MapRepo, zns: List<Map>) {
    for (map in zns) {
        mapRepo.addMap(map)
    }
}

/**
 * Adds the cloud data (perimeter points) to the local database
 *
 * @param pPointRepo Perimeter points' repository (Room)
 * @param pPoints Perimeter Points' list from Firestore
 */
fun pPointsFromCloud(pPointRepo: PerimeterPointRepo, pPoints: List<PerimeterPoint>?) {
    if (pPoints != null) {
        for (pPoint in pPoints) {
            pPointRepo.addPPoint(pPoint)
        }
    }
}
/**
 * Adds the cloud data (points) to the local database
 *
 * @param pointValueRepo Points' repository (Room)
 * @param points Points' list from Firestore
 */
fun pointsValueFromCloud(pointValueRepo: PointValueRepo, points: List<PointValue>?){
    if(points != null) {
        for (point in points) {
            pointValueRepo.addPoint(point)
        }
    }
}
/**
 * Adds the cloud data (samples) to the local database
 *
 * @param sampleRepo Samples' Repo (Room)
 * @param samples Samples' List from Firestore
 */
fun samplesFromCloud(sampleRepo: SampleRepo,  samples: List<Sample>?){
    if(samples != null) {

        for (sample in samples) {
            sampleRepo.addSample(sample)
        }
    }
}