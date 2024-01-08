package it.unipi.halofarms.data.sample

import it.unipi.halofarms.data.cloud.FirestoreProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class SampleRepo(private val sampleDao: SampleDao) {
    // Coroutine scope
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    // Proxy
    private val proxy = FirestoreProxy()

    // Gets samples
    fun getSamples(date: String, mapName: String) = sampleDao.getSamples(date, mapName)

    // Gets sample
    fun getSample(date: String, lat: Double, lng: Double) = sampleDao.getSample(lat, lng, date)

    /**
     * Adds a sample
     *
     * @param sample New sample
     */
    fun addSample(
        sample:Sample
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            sampleDao.addSample(sample)
        }
        coroutineScope.launch(Dispatchers.IO){
            proxy.addSample(sample)
        }
    }

    fun deleteSample(latitude: Double, longitude: Double, date: String) {
        coroutineScope.launch(Dispatchers.IO) {
            sampleDao.deleteSample(latitude, longitude, date)
        }
        coroutineScope.launch(Dispatchers.IO){
            proxy.delDocReference("samples", listOf(("${latitude},${longitude}")))
        }
    }

/*    *//**
     * Updates current point's ph value
     *
     * @param latitude Current point lat
     * @param longitude Current point lng
     * @param ph, new ph value
     *//*
    fun updatePh(latitude: Double, longitude: Double, ph: Double, date: String) {
        coroutineScope.launch(Dispatchers.IO) {
            sampleDao.updatePhList(latitude, longitude, ph, date)
        }
    }
    *//**
     * Updates current point's sar value
     *
     * @param latitude Current point lat
     * @param longitude Current point lng
     * @param sar, new sar value
     *//*
    fun updateSar(latitude: Double, longitude: Double, sar: Double, date: String) {
        coroutineScope.launch(Dispatchers.IO) {
            sampleDao.updateSarList(latitude, longitude, sar, date)
        }
    }


    *//**
     * Updates current point's ec value
     *
     * @param latitude Current point lat
     * @param longitude Current point lng
     * @param ec, new ev value
     *//*
    fun updateEc(latitude: Double, longitude: Double, ec: Double, date: String) {
        coroutineScope.launch(Dispatchers.IO) {
            sampleDao.updateEcList(latitude, longitude, ec, date)
        }
    }

    *//**
     * Updates current point's cec value
     *
     * @param latitude Current point lat
     * @param longitude Current point lng
     * @param cec, new cec value
     *//*
    fun updateCec(latitude: Double, longitude: Double Cec: Double, date: String) {
        coroutineScope.launch(Dispatchers.IO) {
            sampleDao.updateCecList(latitude, longitude Cec, date)
        }
    }*/

    /**
     * Updates current point's date value
     *
     * @param latitude Current point lat
     * @param longitude Current point lng
     * @param date New date
     */
    fun updateDate(latitude: Double, longitude: Double, date: String) {
        coroutineScope.launch(Dispatchers.IO) {
            sampleDao.updateDate(latitude, longitude, date)
        }
        coroutineScope.launch(Dispatchers.IO){
            proxy.updateDate(latitude, longitude, date)
        }
    }

   /**
     * Updates current point's to be analyzed flag
     *
     * @param latitude Current point lat
     * @param longitude Current point lng
     * @param toBeAnalyzed To be analyzed flag
     */
    fun updateToBeAnalyzed(latitude: Double, longitude: Double, toBeAnalyzed: Boolean) {
        coroutineScope.launch(Dispatchers.IO) {
            sampleDao.updateToBeAnalyzed(latitude, longitude, toBeAnalyzed)
        }
       coroutineScope.launch(Dispatchers.IO){
           proxy.updateToBeAnalyzed(latitude, longitude, toBeAnalyzed)
       }
    }

    fun deleteSamples(mapName: String) {
        coroutineScope.launch(Dispatchers.IO) {
            sampleDao.deleteAllSamples(mapName)
        }
    }
}