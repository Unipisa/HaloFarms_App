package it.unipi.halofarms.data.perimeterPoint

import it.unipi.halofarms.data.cloud.FirestoreProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class PerimeterPointRepo (private val perimeterPointDao: PerimeterPointDao) {
    // Coroutine scope
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    // Proxy
    private val proxy = FirestoreProxy()

    // Gets perimeter points
    fun getPPoints(): Flow<List<PerimeterPoint>>  = perimeterPointDao.getAllPPoints()

    /**
     * Adds a perimeter point
     *
     * @param newPoint New perimeter point
     */
    fun addPPoint(newPoint: PerimeterPoint) {
        coroutineScope.launch(Dispatchers.IO) {
            perimeterPointDao.addPPoint(newPoint)
        }
        coroutineScope.launch(Dispatchers.IO){
            proxy.addPerimeterPoint(newPoint)
        }
    }

    /**
     * Deletes current map's perimeter points
     *
     * @param mapName Current map's name
     * @param perimeterPoints List of perimeter points that will be deleted
     */
    fun deletePPoints(mapName: String, perimeterPoints: List<PerimeterPoint>) {
        coroutineScope.launch(Dispatchers.IO) {
            perimeterPointDao.deletePPoints(mapName)
        }
        coroutineScope.launch(Dispatchers.IO){
            proxy.delDocReference("perimeterPoints", perimeterPoints)
        }
    }
}