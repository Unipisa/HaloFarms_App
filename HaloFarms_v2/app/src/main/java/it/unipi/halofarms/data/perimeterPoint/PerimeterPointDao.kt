package it.unipi.halofarms.data.perimeterPoint

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PerimeterPointDao {
    // Adds a perimeter point to the firestore
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPPoint(ppoint: PerimeterPoint)

    // Deletes all of the current map's perimeter points
    @Query("DELETE FROM perimeterPoints WHERE zoneName = :name")
    suspend fun deletePPoints(name: String)

    // Gets all points
    @Query("SELECT * FROM perimeterPoints")
    fun getAllPPoints(): Flow<List<PerimeterPoint>>
}