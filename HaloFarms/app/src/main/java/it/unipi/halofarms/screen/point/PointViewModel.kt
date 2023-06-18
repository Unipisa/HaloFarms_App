package it.unipi.halofarms.screen.point

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import it.unipi.halofarms.data.FirestoreProxy

class PointViewModel(
    private val proxy: FirestoreProxy
) {
    // Points from the firestore
    val points = proxy.pnts

    // New value for the date of analysis
    var newDate by mutableStateOf("")
        private set

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
        private set

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
        private set

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
        private set

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
        private set

    /**
     * Updates the new sar value of pointName
     *
     * @param newSar New pH value
     */
    fun updateNewSar(newSar: String) {
        this.newSar = newSar
    }

    /**
     * Adds a new point
     *
     * @param pointName Point's name
     * @param mapName Map's name
     */
    fun addPoint(pointName: String, mapName: String){
        proxy.addPoint(Point(pointName, mapName, null, null, null, null, null, null, null, null))
    }

    /**
     * Deletes a point
     *
     * @param pointName Point's name
     */
    fun deletePoint(pointName: String, mapName: String) {
        proxy.delDocReference("points", listOf(pointName))
        proxy.delPointInMap(mapName, pointName)
    }

    /**
     * Updates a point's color
     *
     * @param pointName Point's name
     * @param color New point's color
     */
    fun updateColor(pointName: String, color: String){
        proxy.updateColor(pointName, color)
    }

    /**
     * Adds a perimeter point to a map
     *
     * @param pointName Point's name
     * @param mapName Map's name
     */
    fun addPerimeterPoint(pointName: String, mapName: String) {
        proxy.updatePerimeterPointsinMap(mapName, pointName)

    }

    /**
     * Adds a sampling
     *
     * @param pointName Point's name
     * @param newPh New value for pH
     * @param newCec New value for cec
     * @param newEc New value for ec
     * @param newSar New value for sar
     */
    fun addSampling(
        pointName: String,
        newPh: String,
        newCec: String,
        newEc: String,
        newSar: String,
        newDate: String
    ) {
        proxy.addSampling(pointName, newPh, newCec, newEc, newSar, newDate)
    }

    /**
     * Updates the 'analyzed' flag
     *
     * @param name Point's name
     * @param analyze If the point's analyzed
     */
    fun updateAnalyze(name: String, analyze: String) {
        proxy.updateAnalyzed(name, analyze)
    }

    /**
     * Updates the 'to be analyzed' flag
     *
     * @param name Point's name
     * @param toBeAnalyzed If the point's to be analyzed
     */
    fun updateToBeAnalyzed(name: String, toBeAnalyzed: String) {
        proxy.updateToBeAnalyzed(name, toBeAnalyzed)
    }
}