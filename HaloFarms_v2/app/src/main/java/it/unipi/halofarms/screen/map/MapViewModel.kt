package it.unipi.halofarms.screen.map

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import it.unipi.halofarms.R
import it.unipi.halofarms.data.map.Map
import it.unipi.halofarms.data.map.MapRepo
import it.unipi.halofarms.data.perimeterPoint.PerimeterPoint
import it.unipi.halofarms.data.perimeterPoint.PerimeterPointRepo
import it.unipi.halofarms.data.point.PointValueRepo
import it.unipi.halofarms.data.sample.SampleRepo
import it.unipi.halofarms.util.findCentroid
import it.unipi.halofarms.util.toaster
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

private const val TAG = "MapViewModel"

class MapViewModel(
    private val mapRepo: MapRepo,
    private val pointValueRepo: PointValueRepo,
    private val perimeterPointRepo: PerimeterPointRepo,
    private val samplesRepo: SampleRepo
) : ViewModel() {
    // Name for the new map
    var newName by mutableStateOf("")

    /**
     * Updates the new map's name
     *
     * @param input Map's name
     */
    fun updateNewName(input: String){
        newName = input
    }

    // Maps list
    fun mapList() : LiveData<List<Map>> = mapRepo.getMaps().asLiveData()

    // Finds a map
    fun map(mapName: String) : Flow<Map> = mapRepo.getMap(mapName)

    // Perimeter points of the current map
    fun perimeterPoints() : LiveData<List<PerimeterPoint>> = perimeterPointRepo.getPPoints().asLiveData()


    /**
     * Adds a new map
     *
     * @param scope Coroutine Scope
     * @param newName New map's name
     * @param longitude User's long
     * @param latitude User's lat
     * @param context Current context
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun addNewMap(
        scope: CoroutineScope,
        newName: String,
        latitude: Double?,
        longitude: Double?,
        context: Context
    ){
        if(longitude == null) {
            // Gets user last location
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if(location != null) {
                    val map = Map(newName, location.latitude, location.longitude, "handfree", 0.00, false, "${LocalDate.now().dayOfMonth}-${LocalDate.now().month.value}-${LocalDate.now().year}")
                    mapRepo.addMap(map)

                    toaster(scope, context, R.string.added_new_map)
                } else {
                    toaster(scope, context, R.string.is_your_gps_on)
                }
            }
        } else {
            val map = Map(newName, latitude!!, longitude, "handfree", 0.00, false, "${LocalDate.now().dayOfMonth}-${LocalDate.now().month.value}-${LocalDate.now().year}")
            mapRepo.addMap(map)
            toaster(scope, context, R.string.added_new_map)
        }
    }

    /**
     * Deletes a map
     *
     * @param mapName Current map's name
     * @param perimeterPoints Perimeter points
     */
    fun deleteMap(mapName: String, perimeterPoints: List<PerimeterPoint>){
        mapRepo.deleteMap(mapName)
        perimeterPointRepo.deletePPoints(mapName, perimeterPoints)
        pointValueRepo.deletePoints(mapName)
        samplesRepo.deleteSamples(mapName)
    }

    /**
     * Deletes map value points
     *
     * @param mapName Current map's name
     */
    fun deleteMapPoints(mapName: String) {
        pointValueRepo.deletePoints(mapName)
    }

    /**
     * Updates current map's mode flag
     *
     * @param mapName Current map's name
     * @param mode Current map's mode flag
     */
    fun setMode(mapName: String?, mode: String) {
        try {
            mapRepo.updateMode(mapName!!, mode)

        } catch (e: NullPointerException) {
            Log.e(TAG, "Cannot update the drawing map, because it's null")
        }
    }

    /**
     * Updates current map's coordinates
     *
     * @param mapName Current map's name
     * @param perimeterPoints Perimeter points
     */
    fun updateLatLng(mapName: String, perimeterPoints: ArrayList<LatLng>){
        val point = findCentroid(perimeterPoints.toList())
        mapRepo.updateLatLng(mapName, point.latitude, point.longitude)
    }

    /**
     * Updates current map's done flag
     *
     * @param mapName Current map's name
     * @param done Current map's done flag
     */
    fun updateDone(mapName: String, done: Boolean){
        mapRepo.updateDone(mapName, done)

    }

    /**
     * Adds a perimeter point
     *
     * @param latitude Perimeter point's lat
     * @param longitude Perimeter point's lng
     * @param mapName Current map's name
     */
    fun addPerimeterPoint(latitude: Double, longitude: Double, mapName: String) {
        perimeterPointRepo.addPPoint(PerimeterPoint(latitude, longitude, mapName))

    }

    /**
     * Updates map's area
     *
     * @param mapName  Map's name
     * @param area  Map's area
     */
    fun updateArea(mapName: String, area: Double) {
        mapRepo.updateArea(mapName, area)
    }
}
