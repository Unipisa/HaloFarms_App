package it.unipi.halofarms.data.point

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PointValueDao {
    // Adds a value point
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPoint(point: PointValue)


    // Gets a point
    @Query("SELECT * FROM pointsValue WHERE latitude = :latitude AND longitude = :longitude")
    fun findPoint(latitude: Double, longitude: Double): Flow<PointValue>

    // Gets all of the value points for a certain map
    @Query("SELECT * FROM pointsValue WHERE zoneName = :mapName")
    fun getPoints(mapName: String): Flow<List<PointValue>>

    // Deletes all of the value points
    @Query("DELETE FROM pointsValue WHERE zoneName = :name")
    suspend fun deletePoints(name: String)


}