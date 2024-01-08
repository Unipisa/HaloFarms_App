package it.unipi.halofarms.data.map

import it.unipi.halofarms.data.cloud.FirestoreProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class MapRepo(private val mapDao: MapDao) {
    // Coroutine scope
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    // Proxy
    private val proxy = FirestoreProxy()


    // Gets all of the user's maps
    fun getMaps(): Flow<List<Map>> = mapDao.getAllMaps()


    // Gets a certain map
    fun getMap(mapName: String) = mapDao.findMap(mapName)

    /**
     * Adds a new map to the firestore
     *
     * @param newMap New map
     */
    fun addMap(newMap: Map) {
        coroutineScope.launch(Dispatchers.IO) {
            mapDao.addMap(newMap)
        }
        coroutineScope.launch(Dispatchers.IO){
            proxy.addMap(newMap)
        }
    }

    /**
     * Deletes a map
     *
     * @param mapName Current map's name
     */
    fun deleteMap(mapName: String){
        coroutineScope.launch(Dispatchers.IO) {
            mapDao.deleteMap(mapName)
        }
        coroutineScope.launch(Dispatchers.IO){
            proxy.delDocReference("maps", listOf(mapName))
        }
    }

    /**
     * Updates current map's coordinates
     *
     * @param mapName Current map's name
     * @param latitude Map's latitude
     * @param longitude Map's longitude
     */
    fun updateLatLng(mapName: String, latitude: Double, longitude: Double){
        coroutineScope.launch(Dispatchers.IO) {
            mapDao.updateLatLng(mapName, latitude, longitude)
        }

        coroutineScope.launch(Dispatchers.IO){
            proxy.updateLatLng(mapName, latitude, longitude)
        }
    }

    /**
     * Updates current map's mode flag
     *
     * @param mapName Current map's name
     * @param mode Current map's mode flag
     */
    fun updateMode(mapName: String, mode: String){
        coroutineScope.launch(Dispatchers.IO) {
            mapDao.updateMode(mapName, mode)
        }
        coroutineScope.launch(Dispatchers.IO){
            proxy.updateMode(mapName, mode)
        }
    }

    /**
     * Updates current map's done flag
     *
     * @param mapName Current map's name
     * @param done Current map's done flag
     */
    fun updateDone(mapName: String, done: Boolean){
        coroutineScope.launch(Dispatchers.IO) {
            mapDao.updateDone(mapName, done)
        }
        coroutineScope.launch(Dispatchers.IO){
            proxy.updateDone(mapName, done)
        }
    }

    /**
     * Updates current map's area
     *
     * @param mapName Current map's name
     * @param area Current map's area
     */
    fun updateArea(mapName: String, area: Double){
        coroutineScope.launch(Dispatchers.IO) {
            mapDao.updateArea(mapName, area)
        }
        coroutineScope.launch(Dispatchers.IO){
            proxy.updateArea(mapName, area)
        }
    }
}
