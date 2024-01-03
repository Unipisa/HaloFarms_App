package it.unipi.halofarms.data.point

import it.unipi.halofarms.data.cloud.FirestoreProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class PointValueRepo(private val pointDao: PointValueDao) {
    // Coroutine scope
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    // Proxy
    private val proxy = FirestoreProxy()

    // Gets all of the value points
    fun getPoints(mapName: String): Flow<List<PointValue>> = pointDao.getPoints(mapName)

    fun getPoint(latitude: Double, longitude: Double) = pointDao.findPoint(latitude, longitude)

    /**
     * Adds a point
     *
     * @param newPoint Point that will be added
     */
    fun addPoint(newPoint: PointValue) {
        coroutineScope.launch(Dispatchers.IO) {
            pointDao.addPoint(newPoint)
        }
        coroutineScope.launch(Dispatchers.IO){
            proxy.addPoint(newPoint)
        }
    }

    /**
     * Deletes all of the value points of the current map
     *
     * @param zoneName Current map's name
     */
    fun deletePoints(zoneName: String) {
        coroutineScope.launch(Dispatchers.IO) {
            pointDao.deletePoints(zoneName)
        }
        coroutineScope.launch(Dispatchers.IO){
            proxy.delPoints(zoneName)
        }
    }

}