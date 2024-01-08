package it.unipi.halofarms.data.cloud

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unipi.halofarms.R
import it.unipi.halofarms.data.map.Map
import it.unipi.halofarms.data.map.MapRepo
import it.unipi.halofarms.data.perimeterPoint.PerimeterPoint
import it.unipi.halofarms.data.perimeterPoint.PerimeterPointRepo
import it.unipi.halofarms.data.point.PointValue
import it.unipi.halofarms.data.point.PointValueRepo
import it.unipi.halofarms.data.sample.Sample
import it.unipi.halofarms.data.sample.SampleRepo
import it.unipi.halofarms.register.RegisterActivity
import it.unipi.halofarms.util.mapsFromCloud
import it.unipi.halofarms.util.pPointsFromCloud
import it.unipi.halofarms.util.pointsValueFromCloud
import it.unipi.halofarms.util.samplesFromCloud
import it.unipi.halofarms.util.toaster
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


// Tag used while interacting with the LOG
private const val TAG = "FirestoreProxy"

@Singleton
class FirestoreProxy {

    companion object {
        // Username
        var username = RegisterActivity.username
        // Shared Preferences
        var sharedPreferences = RegisterActivity.preferences
    }

    /**
     * Gets the firestore database
     *
     * @return firestore The Firestore instance of the default FirebaseApp
     */
    @Singleton
    fun firestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    /**
     * Deletes one or more objects
     *
     * @param collection The current object's collection
     * @param reference The object that will be deleted
     */
    fun delDocReference(collection: String, reference: List<Any>) {
        for (ref in reference) {

            firestore().collection("$username-${collection}").document(ref.toString())
                .delete()
                .addOnSuccessListener { Log.d(TAG, "Object $ref successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting object $ref", e) }
        }
    }

    /**
     * Deletes points from a 'mapName'
     *
     * @param mapName Map's name
     */
    fun delPoints(mapName: String) {
        val query = firestore()
            .collection("$username-points").whereEqualTo("zoneName", mapName)
        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference
                        .delete()
                        .addOnSuccessListener { Log.d(TAG, "PointValue $document successfully deleted!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting point $document", e) }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }

        val querySamples = firestore()
            .collection("$username-samples").whereEqualTo("zoneName", mapName)
        querySamples.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference
                        .delete()
                        .addOnSuccessListener { Log.d(TAG, "Sample $document successfully deleted!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting sample $document", e) }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }

        val queryPerimeterPoint = firestore().collection("$username-perimeterPoints").whereEqualTo("zoneName", mapName)
        queryPerimeterPoint.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference
                        .delete()
                        .addOnSuccessListener { Log.d(TAG, "Perimeter point $document successfully deleted!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting perimeter point $document", e) }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }

    /**
     * Adds a new map
     *
     * @param map New map
     */
    fun addMap(map: Map) {
        val z = hashMapOf(
            "name" to map.name,
            "latitude" to map.latitude,
            "longitude" to map.longitude,
            "mode" to map.mode,
            "area" to map.area,
            "date" to map.date,
            "done" to map.done
        )

        // Sends the map to the Firestore
        firestore().collection("$username-maps").document(map.name).set(z)
            .addOnSuccessListener {
                Log.d(TAG, "Map ${map.name} written")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding map ${map.name}", e)
            }
    }

    /**
     * Adds a new perimeter point in cloud
     *
     * @param point New perimeter point
     */
    fun addPerimeterPoint(point: PerimeterPoint) {
        val p = hashMapOf(
            "latitude" to point.latitude,
            "longitude" to point.longitude,
            "zoneName" to point.zoneName,
        )

        // Sends the map to the Firestore
        firestore().collection("$username-perimeterPoints").document("${point.latitude},${point.longitude}").set(p)
            .addOnSuccessListener {
                Log.d(TAG, "Perimeter Point (${point.latitude},${point.longitude}) written")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding point (${point.latitude},${point.longitude})", e)
            }
    }

    /**
     * Adds a new sample
     *
     * @param sample New sample
     */
    fun addSample(sample: Sample) {
        val p = hashMapOf(
            "latitude" to sample.latitude,
            "longitude" to sample.longitude,
            "zoneName" to sample.zoneName,
            "sar" to sample.sar,
            "ph" to sample.ph,
            "ec" to sample.ec,
            "cec" to sample.cec,
            "date" to sample.date,
            "toBeAnalyzed" to sample.toBeAnalyzed,
            "zoneName" to sample.zoneName
        )

        // Sends the map to the Firestore
        firestore().collection("$username-samples").document("${sample.latitude},${sample.longitude}").set(p)
            .addOnSuccessListener {
                Log.d(TAG, "Sample (${sample.latitude},${sample.longitude}) written")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding sample (${sample.latitude},${sample.longitude})", e)
            }
    }

    /**
     * Adds a new point in cloud
     *
     * @param point New point
     */
    fun addPoint(point: PointValue) {
        val p = hashMapOf(
            "latitude" to point.latitude,
            "longitude" to point.longitude,
            "zoneName" to point.zoneName,
            "qrCode" to point.qrCode,
        )

        // Sends the map to the Firestore
        firestore().collection("$username-points").document("${point.latitude},${point.longitude}").set(p)
            .addOnSuccessListener {
                Log.d(TAG, "PointValue (${point.latitude},${point.longitude}) written")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding point (${point.latitude},${point.longitude})", e)
            }
    }

    /**
     * Update's flag 'to be analyzed'
     *
     * @param latitude Point's lat
     * @param longitude Point's lng
     * @param toBeAnalyzed New value for the flag
     */
    fun updateToBeAnalyzed(latitude: Double, longitude: Double, toBeAnalyzed: Boolean) {
        val pointRef = firestore().collection("$username-samples").document("${latitude},${longitude}")

        pointRef
            .update("toBeAnalyzed", toBeAnalyzed)
            .addOnSuccessListener { Log.d(TAG, "Sample (${latitude},${longitude}) successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating sample (${latitude},${longitude})", e) }
    }

    /**
     * Updates point's date list
     *
     * @param latitude Point's lat
     * @param longitude Point's lng
     * @param dateOfAnalysis Value to add to the list
     */
    fun updateDate(latitude: Double, longitude: Double, dateOfAnalysis: String) {
        val pointRef = firestore().collection("$username-samples").document("${latitude},${longitude}")

        pointRef
            .update("date", dateOfAnalysis)
            .addOnSuccessListener { Log.d(TAG, "Sample (${latitude},${longitude}) successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating sample (${latitude},${longitude})", e) }
    }

/*
    fun updateSar(latitude: Double, longitude: Double, newSar: Double) {
        val pointRef = firestore().collection("$username-points").document("${latitude},${longitude}")

        pointRef
            .update("sar", newSar)
            .addOnSuccessListener { Log.d(TAG, "PointValue (${latitude},${longitude}) successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating point (${latitude},${longitude} )", e) }
    }

    *//**
     * Updates point's ph list
     *
     *
     *//*
    fun updatePh(latitude: Double, longitude: Double, newPh: Double) {
        val pointRef = firestore().collection("$username-points").document("${latitude},${longitude} ")

        pointRef
            .update("ph", newPh)
            .addOnSuccessListener { Log.d(TAG, "PointValue (${latitude},${longitude} ) successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating point (${latitude},${longitude} )", e) }
    }

    *//**
     * Updates point's cec list
     *
     *//*
    fun updateCec(latitude: Double, longitude: Double, newCec: Double) {
        val pointRef = firestore().collection("$username-points").document("${latitude},${longitude} ")

        pointRef
            .update("cec", newCec)
            .addOnSuccessListener { Log.d(TAG, "PointValue (${latitude},${longitude} ) successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating point (${latitude},${longitude} )", e) }
    }

    *//**
     * Updates point's ec list
     *
     *//*
    fun updateEc(latitude: Double, longitude: Double, newEc: Double) {
        val pointRef = firestore().collection("$username-points").document("${latitude},${longitude} ")

        pointRef
            .update("ec", newEc)
            .addOnSuccessListener { Log.d(TAG, "PointValue (${latitude},${longitude} ) successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating point (${latitude},${longitude} ", e) }
    }*/

    /**
     * Updates map's area
     *
     */
    fun updateArea(mapName: String, newArea: Double) {
        val pointRef = firestore().collection("$username-maps").document(mapName)

        pointRef
            .update("area", newArea)
            .addOnSuccessListener { Log.d(TAG, "Map $mapName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating map $mapName", e) }
    }

    /**
     * Updates the drawing mode's map
     *
     * @param zoneName The map name
     * @param mode The drawing mode of the map
     */
    fun updateMode(zoneName: String, mode: String) {
        val plantRef = firestore().collection("$username-maps").document(zoneName)

        plantRef
            .update("mode", mode)
            .addOnSuccessListener { Log.d(TAG, "Map $zoneName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating plant $zoneName", e) }
    }

    /**
     * Updates done flag of mapName
     *
     * @param mapName Map's name
     * @param done New value for the done flag
     */
    fun updateDone(mapName: String, done: Boolean) {
        val pointRef = firestore().collection("$username-maps").document(mapName)

        pointRef
            .update("done", done)
            .addOnSuccessListener { Log.d(TAG, "Map $mapName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating plant $mapName", e) }
    }

    /**
     * Updates coordinates of mapName
     *
     * @param mapName Map's name
     * @param latitude New value map's lat
     * @param longitude New value map's lng
     */
    fun updateLatLng(mapName: String, latitude: Double, longitude: Double) {
        val mapRef = firestore().collection("$username-maps").document(mapName)

        mapRef
            .update("latitude", latitude)
            .addOnSuccessListener { Log.d(TAG, "Map $mapName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating map $mapName", e) }

        mapRef
            .update("longitude", longitude)
            .addOnSuccessListener { Log.d(TAG, "Map $mapName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating map $mapName", e) }
    }

    /* Samples stored in Firestore */
    /*val smpls: Flow<List<Sample>>
        get() = firestore()
            .collection("$username-samples").snapshots().map { snapshot ->
            snapshot.toObjects(Sample::class.java)
        }*/

    @Composable
    fun FromCloud(
        pointValueRepo: PointValueRepo,
        mapRepo: MapRepo,
        perimeterRepo: PerimeterPointRepo,
        sampleRepo: SampleRepo,
        context: Context
    ) {
        // Samples
        firestore().collection("$username-samples").get()
            .addOnSuccessListener { result -> samplesFromCloud(sampleRepo, result.toObjects(Sample::class.java)) }
            .addOnFailureListener {  toaster(CoroutineScope(Dispatchers.Main), context, R.string.you_need_internet_for_your_first_run) }

        // Maps
        firestore().collection("$username-maps").get()
            .addOnSuccessListener { result -> mapsFromCloud(mapRepo, result.toObjects(Map::class.java)) }
            .addOnFailureListener { toaster(CoroutineScope(Dispatchers.Main), context, R.string.you_need_internet_for_your_first_run) }
        // Points
        firestore().collection("$username-points").get()
            .addOnSuccessListener { result -> pointsValueFromCloud(pointValueRepo, result.toObjects(PointValue::class.java)) }
            .addOnFailureListener {  toaster(CoroutineScope(Dispatchers.Main), context, R.string.you_need_internet_for_your_first_run) }
        // Perimeter Points
        firestore().collection("$username-perimeterPoints").get()
            .addOnSuccessListener { result -> pPointsFromCloud(perimeterRepo, result.toObjects(PerimeterPoint::class.java)) }
            .addOnFailureListener {  toaster(CoroutineScope(Dispatchers.Main), context, R.string.you_need_internet_for_your_first_run) }

    }

}