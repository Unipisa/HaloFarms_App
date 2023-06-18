package it.unipi.halofarms.data


import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import it.unipi.halofarms.register.RegisterActivity
import it.unipi.halofarms.screen.point.Point
import it.unipi.halofarms.screen.zone.Map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

// Tag used while interacting with the LOG
private const val TAG = "FirestoreProxy"

@Singleton
class FirestoreProxy {

    companion object {
        // Username
        var username = RegisterActivity.username
    }

    /**
     * Gets the firestore database
     *
     * @return db, the FirebaseFirestore instance of the default FirebaseApp
     */
    @Singleton
    fun db(): FirebaseFirestore {
        val db = Firebase.firestore

        // Enable persistence
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings

        return db
    }

    /* Maps stored in Firestore */
    val zns: Flow<List<Map>>
        get() = db().collection("$username-maps").snapshots().map { snapshot ->
            snapshot.toObjects(Map::class.java)
        }

    /* Points stored in Firestore */
    val pnts: Flow<List<Point>>
        get() = db().collection("$username-points").snapshots().map { snapshot ->
            snapshot.toObjects(Point::class.java)
        }

    /**
     * Deletes one or more objects
     *
     * @param collection The current object's collection
     * @param reference The object that will be deleted
     */
    fun delDocReference(collection: String, reference: List<String>) {
        for (ref in reference)
            db().collection("$username-${collection}").document(ref)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "Object $ref successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting object $ref", e) }
    }

    /**
     * Deletes points from a 'mapName'
     *
     * @param mapName Map's name
     */
    fun delPoints(mapName: String) {
        val query = db().collection("$username-points").whereEqualTo("zoneName", mapName)
        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference
                        .delete()
                        .addOnSuccessListener { Log.d(TAG, "Point $document successfully deleted!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting point $document", e) }
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
            "analysisPoints" to map.points,
            "mode" to map.mode,
            "area" to map.area,
            "perimeterPoints" to map.perimeterPoints
        )

        // Sends the map to the Firestore
        db().collection("$username-maps").document(map.name!!).set(z)
            .addOnSuccessListener {
                Log.d(TAG, "Map ${map.name} written")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding map ${map.name}", e)
            }
    }

    /**
     * Adds a new point in cloud
     *
     * @param point New point
     */
    fun addPoint(point: Point) {
        val p = hashMapOf(
            "name" to point.name,
            "zoneName" to point.zoneName,
            "qrCode" to point.qrCode,
            "analyzed" to point.analyzed,
            "toBeAnalyzed" to point.toBeAnalyzed,
            "sarList" to point.sarList,
            "phList" to point.phList,
            "ecList" to point.ecList,
            "cecList" to point.cecList,
            "dateList" to point.dateList
        )

        // Sends the map to the Firestore
        db().collection("$username-points").document(point.name!!.removeSuffix("${username}/lat/lng: (")).set(p)
            .addOnSuccessListener {
                Log.d(TAG, "Point ${point.name} written")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding point ${point.name}", e)
            }

        // Updates the point's map
        updatePointsinMap(point.zoneName!!, point.name!!) 
    }

    /**
     * Updates points' list in current map
     * @param mapName Map's name
     * @param pointName, current point's name
     */
    private fun updatePointsinMap(mapName: String, pointName: String){
        val map = db().collection("$username-maps").document(mapName)
        map
            .update("points", FieldValue.arrayUnion(pointName))
            .addOnSuccessListener { Log.d(TAG, "Map successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating map", e) }

    }

    /**
     * Updates the current point's qrcode
     *
     * @param pointName Point's name
     * @param qrCode Point's qrCode
     */
    fun updateQrCode(pointName: String, qrCode: String) {
        val pointRef = db().collection("$username-points").document(pointName)

        pointRef
            .update("qrCode", qrCode)
            .addOnSuccessListener { Log.d(TAG, "Point $pointName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating point $pointName", e) }
    }

    /**
     * Updates flag 'analyzed'
     *
     * @param pointName Point's name
     * @param analyzed New value for the flag
     */
    fun updateAnalyzed(pointName: String, analyzed: String) {
        val pointRef = db().collection("$username-points").document(pointName)

        pointRef
            .update("analyzed", analyzed)
            .addOnSuccessListener { Log.d(TAG, "Point $pointName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating point $pointName", e) }
    }

    /**
     * Update's flag 'to be analyzed'
     *
     * @param pointName Point's name
     * @param toBeAnalyzed New value for the flag
     */
    fun updateToBeAnalyzed(pointName: String, toBeAnalyzed: String) {
        val pointRef = db().collection("$username-points").document(pointName)

        pointRef
            .update("toBeAnalyzed", toBeAnalyzed)
            .addOnSuccessListener { Log.d(TAG, "Point $pointName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating point $pointName", e) }
    }

    /**
     * Updates point's date list
     *
     * @param pointName Point's name
     * @param dateOfAnalysis Value to add to the list
     */
    private fun updateDateList(pointName: String, dateOfAnalysis: String) {
        val pointRef = db().collection("$username-points").document(pointName)

        pointRef
            .update("dateList", FieldValue.arrayUnion(dateOfAnalysis))
            .addOnSuccessListener { Log.d(TAG, "Point $pointName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating point $pointName", e) }
    }

    /**
     * Deletes point in the points map list
     *
     * @param mapName Map's name
     * @param pointName Point's name
     */
    fun delPointInMap(mapName: String, pointName: String) {
        val zoneRef = db().collection("$username-zones").document(mapName)

        zoneRef
            .update("points", FieldValue.arrayRemove(pointName))
            .addOnSuccessListener { Log.d(TAG, "Map $mapName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating map $mapName", e) }
    }



    /**
     * Updates point's sar list
     *
     * @param pointName Point's name
     * @param sar Value to add to the list
     */
    private fun updateSarList(pointName: String, sar: String) {
        val pointRef = db().collection("$username-points").document(pointName)

        pointRef
            .update("sarList", FieldValue.arrayUnion(sar))
            .addOnSuccessListener { Log.d(TAG, "Point $pointName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating point $pointName", e) }
    }

    /**
     * Updates point's ph list
     *
     * @param pointName Point's name
     * @param ph Value to add to the list
     */
    private fun updatePhList(pointName: String, ph: String) {
        val pointRef = db().collection("$username-points").document(pointName)

        pointRef
            .update("phList", FieldValue.arrayUnion(ph))
            .addOnSuccessListener { Log.d(TAG, "Point $pointName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating point $pointName", e) }
    }

    /**
     * Updates point's cec list
     *
     * @param pointName Point's name
     * @param cec Value to add to the list
     */
    private fun updateCecList(pointName: String, cec: String) {
        val pointRef = db().collection("$username-points").document(pointName)

        pointRef
            .update("cecList", FieldValue.arrayUnion(cec))
            .addOnSuccessListener { Log.d(TAG, "Point $pointName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating point $pointName", e) }
    }

    /**
     * Updates point's ec list
     *
     * @param pointName Point's name
     * @param ec Value to add to the list
     */
    private fun updateEcList(pointName: String, ec: String) {
        val pointRef = db().collection("$username-points").document(pointName)

        pointRef
            .update("ecList", FieldValue.arrayUnion(ec))
            .addOnSuccessListener { Log.d(TAG, "Point $pointName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating point $pointName", e) }
    }

    /**
     * Updates the drawing mode's map
     *
     * @param zoneName The map name
     * @param mode The drawing mode of the map
     */
    fun updateMode(zoneName: String, mode: String) {
        val plantRef = db().collection("$username-maps").document(zoneName)

        plantRef
            .update("mode", mode)
            .addOnSuccessListener { Log.d(TAG, "Map $zoneName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating plant $zoneName", e) }
    }

    /**
     * Updates point's color
     *
     * @param pointName Point's name
     * @param color New color for pointName
     */
    fun updateColor(pointName: String, color: String){
        val pointRef = db().collection("$username-points").document(pointName)

        pointRef
            .update("color", color)
            .addOnSuccessListener { Log.d(TAG, "Point $pointName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating point $pointName", e) }
    }

    /**
     * Updates perimeter points
     *
     * @param mapName Map's name
     * @param pointName New point's name
     */
    fun updatePerimeterPointsinMap(mapName: String, pointName: String) {
        val map = db().collection("$username-maps").document(mapName)
        map
            .update("perimeterPoints", FieldValue.arrayUnion(pointName))
            .addOnSuccessListener { Log.d(TAG, "Map successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating map", e) }
    }

    /**
     * Adds a sampling
     *
     * @param pointName Point's name
     * @param newPh New value for ph
     * @param newCec New value for cec
     * @param newEc New value for ec
     * @param newSar New value for sar
     * @param newDate New value for the date of analysis
     */
    fun addSampling(
        pointName: String,
        newPh: String,
        newCec: String,
        newEc: String,
        newSar: String,
        newDate: String
    ) {
        updatePhList(pointName, newPh)
        updateCecList(pointName, newCec)
        updateEcList(pointName, newEc)
        updateSarList(pointName, newSar)
        updateDateList(pointName, newDate)
    }

    /**
     * Updates done flag of mapName
     *
     * @param mapName Map's name
     * @param done New value for the done flag
     */
    fun updateDone(mapName: String, done: String) {
        val plantRef = db().collection("$username-maps").document(mapName)

        plantRef
            .update("done", done)
            .addOnSuccessListener { Log.d(TAG, "Map $mapName successfully updated!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating plant $mapName", e) }
    }
}