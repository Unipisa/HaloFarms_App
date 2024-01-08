package it.unipi.halofarms.screen.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import it.unipi.halofarms.data.cloud.FirestoreProxy
import it.unipi.halofarms.data.point.PointValue
import it.unipi.halofarms.data.point.PointValueRepo
import it.unipi.halofarms.data.sample.Sample
import it.unipi.halofarms.data.sample.SampleRepo

class PointViewModel(private val pointValueRepo: PointValueRepo, private val sampleRepo: SampleRepo) {
    // Proxy
    private val proxy = FirestoreProxy()

    // Gets map's points
    fun getPoints(mapName: String) : LiveData<List<PointValue>> = pointValueRepo.getPoints(mapName).asLiveData()

    // Gets samples
    fun getSamples(date: String, mapName: String) : LiveData<List<Sample>> = sampleRepo.getSamples(date, mapName).asLiveData()

    // Gets sample
    //fun getSample(lat: Double, lng: Double, date: String) : Sample = sampleRepo.getSample(date, lat, lng)

    // New value for the date of analysis
    var newDate by mutableStateOf("")

    /**
     * Updates the new date value of pointName
     *
     * @param newDate New date value
     */
    fun updateNewdate(newDate: String) {
        this.newDate = newDate
    }

    // New value for pH
    var newPh by mutableStateOf("")

    /**
     * Updates the new ph value of pointName
     *
     * @param newPh New pH value
     */
    fun updateNewPh(newPh: String) {
        this.newPh = newPh
    }

    // New value for cec
    var newCec by mutableStateOf("")

    /**
     * Updates the new cec value of pointName
     *
     * @param newCec New cec value
     */
    fun updateNewCec(newCec: String) {
        this.newCec = newCec
    }

    // New value for ec
    var newEc by mutableStateOf("")

    /**
     * Updates the new ec value of pointName
     *
     * @param newEc New ec value
     */
    fun updateNewEc(newEc: String) {
        this.newEc = newEc
    }

    // New value for sar
    var newSar by mutableStateOf("")

    /**
     * Updates the new sar value of pointName
     *
     * @param newSar New pH value
     */
    fun updateNewSar(newSar: String) {
        this.newSar = newSar
    }

    /**
     * Adds a point to the map
     *
     * @param latitude Point's latitude
     * @param longitude Point's longitude
     * @param mapName Current map's name
     */
    fun addPoint(latitude: Double, longitude: Double, mapName: String){
        val point = PointValue(
            latitude = latitude,
            longitude = longitude,
            zoneName = mapName,
            qrCode = "null",
        )

        pointValueRepo.addPoint(point)
    }

    /**
     * Adds a sampling
     *
     * @param newPh New value for pH
     * @param newCec New value for cec
     * @param newEc New value for ec
     * @param newSar New value for sar
     */
    fun addSampling(
        latitude: Double,
        longitude: Double,
        newPh: Double,
        newCec: Double,
        newEc: Double,
        newSar: Double,
        newDate: String,
        mapName: String
    ) {
        sampleRepo.addSample(
            Sample(
            latitude,
            longitude,
            newSar,
            newCec,
            newEc,
            newPh,
            newDate,
            false,
            mapName)
        )
    }

    /**
     * Updates samples' date
     *
     * @param latitude Current sample's lat
     * @param longitude Current sample's lng
     * @param date Current sample's date
     */
    fun updateDate(latitude: Double, longitude: Double, date: String) {
        sampleRepo.updateDate(latitude, longitude, date)
    }

    /**
     * Updates the to be analyzed flag
     *
     * @param latitude Current point's lat
     * @param longitude Current point's lng
     * @param toBeAnalyzed toBeAnalyzed flag
     */
    fun updateToBeAnalyzed(latitude: Double, longitude: Double, toBeAnalyzed: Boolean) {
        sampleRepo.updateToBeAnalyzed(latitude, longitude, toBeAnalyzed)
        proxy.updateToBeAnalyzed(latitude, longitude, toBeAnalyzed)
    }

    /**
     * Remove point's values
     *
     * @param latitude Current point's lat
     * @param longitude Current point's lng
     */
    fun deleteSamples(latitude: Double, longitude: Double, date: String) {
        sampleRepo.deleteSample(latitude, longitude, date)
    }
}