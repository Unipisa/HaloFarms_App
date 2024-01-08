package it.unipi.halofarms.data.map

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface MapDao {
    // Adds a map to the firestore
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMap(map: Map)

    // Finds a certain map in the firestore
    @Query("SELECT * FROM maps WHERE name = :mapName")
    fun findMap(mapName: String): Flow<Map>

    // Returns all the maps
    @Query("SELECT * FROM maps")
    fun getAllMaps(): Flow<List<Map>>

    // Deletes a certain map
    @Query("DELETE FROM maps WHERE name = :mapName")
    suspend fun deleteMap(mapName: String)

    // Updates the map's coordinates
    @Query("UPDATE maps SET latitude = :latitude, longitude = :longitude WHERE name = :mapName")
    suspend fun updateLatLng(mapName: String, latitude: Double, longitude: Double)

    // Updates the map's mode flag
    @Query("UPDATE maps SET mode = :mode WHERE name = :mapName")
    suspend fun updateMode(mapName: String, mode: String)

    // Updates the map's done flag
    @Query("UPDATE maps SET done = :done WHERE name = :mapName")
    suspend fun updateDone(mapName: String, done: Boolean)

    // Updates the map's area
    @Query("UPDATE maps SET area = :area WHERE name = :mapName")
    suspend fun updateArea(mapName: String, area: Double)
}