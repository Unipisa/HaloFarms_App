package it.unipi.halofarms.screen.zone

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import it.unipi.halofarms.data.FirestoreProxy

private const val TAG = "MapViewModel"

class MapViewModel constructor(
    private val proxy: FirestoreProxy,
) : ViewModel() {

    // Maps from the firestore
    val maps = proxy.zns

    // Name for the new map
    var newName by mutableStateOf("")
        private set

    /**
     * Updates the new map's name
     *
     * @param input Map's name
     */
    fun updateNewName(input: String){
        newName = input
    }

    /**
     * Adds a new map to the firestore
     *
     * @param newName New map's name
     * @param longitude User's long
     * @param latitude User's lat
     * @param context Current context
     */
    @SuppressLint("MissingPermission")
    fun addNewMap(newName: String, longitude: String?, latitude: String?, context: Context){
        Log.e(TAG, "LONG: $longitude")
        if(longitude == null) {
            // Gets user last location
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if(location != null) {
                    Log.e(TAG, "LOCATION: $location")
                    val map = Map(newName, location.latitude.toString(), location.longitude.toString(), null, null, "1", null, "false")
                    proxy.addMap(map)
                }
            }
        } else {
            val map = Map(newName, latitude, longitude, listOf(), null, "1")
            proxy.addMap(map)
        }

    }

    /**
     * Address for the new map
     */
    var address by mutableStateOf("")
        private set

    /**
     * Updates address
     *
     * @param input New address
     */
    fun updateAddress(input: String){
        address = input
    }

    /**
     * Deletes a map
     *
     * @param mapName Map's name
     */
    fun deleteMap(mapName: String){
        proxy.delDocReference("maps", listOf(mapName))
        proxy.delPoints(mapName)
    }

    fun deleteMapPoints(mapName: String) {
        proxy.delPoints(mapName)
    }

    /**
     * Sets the drawing mode
     *
     * @param mapName Map's name
     */
    fun setMode(mapName: String?, mode: String) {
        try {
            proxy.updateMode(mapName!!, mode)
        } catch (e: NullPointerException) {
            Log.e(TAG, "Cannot update the drawing map, because it's null")
        }
    }

    /**
     * Updates the done flag: if it's true the perimeter is done, else it's not
     *
     * @param mapName Map's name
     * @param done New value for the flag
     */
    fun updateDone(mapName: String, done: String){
        proxy.updateDone(mapName, done)
    }
}
