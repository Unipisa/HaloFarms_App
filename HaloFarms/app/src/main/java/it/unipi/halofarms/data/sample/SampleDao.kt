package it.unipi.halofarms.data.sample

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SampleDao {
    // Adds a sample
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSample(sample: Sample)

    // Gets samples
    @Query("SELECT * FROM samples WHERE date = :date AND zoneName = :mapName")
    fun getSamples(date: String, mapName: String) : Flow<List<Sample>>

    // Gets a sample
    @Query("SELECT * FROM samples WHERE latitude = :latitude AND (longitude = :longitude AND date = :date)")
    fun getSample(latitude: Double, longitude: Double, date: String) : Flow<Sample>

    // Updates sample's sar value
    @Query("UPDATE samples SET sar = :sar AND date = :date WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun updateSarList(latitude: Double, longitude: Double, sar: Double, date: String)

    // Updates sample's ph value
    @Query("UPDATE samples SET ph = :ph AND date = :date WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun updatePhList(latitude: Double, longitude: Double, ph: Double, date: String)

    // Updates sample's ec value
    @Query("UPDATE samples SET ec = :ec AND date = :date WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun updateEcList(latitude: Double, longitude: Double, ec: Double, date: String)

    // Updates sample's cec value
    @Query("UPDATE samples SET cec = :cec AND date = :date WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun updateCecList(latitude: Double, longitude: Double, cec: Double, date: String)

    // Deletes all the value points
    @Query("DELETE FROM samples WHERE latitude = :latitude AND (longitude = :longitude AND date = :date)")
    suspend fun deleteSample(latitude: Double, longitude: Double, date: String)

    // Updates sample's toBeAnalyzed flag
    @Query("UPDATE samples SET toBeAnalyzed = :toBeAnalyzed WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun updateToBeAnalyzed(latitude: Double, longitude: Double, toBeAnalyzed: Boolean)

    // Updates sample's date value
    @Query("UPDATE samples SET date = :date WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun updateDate(latitude: Double, longitude: Double, date: String)
    @Query("DELETE FROM samples WHERE zoneName = :mapName")
    suspend fun deleteAllSamples(mapName: String)
}