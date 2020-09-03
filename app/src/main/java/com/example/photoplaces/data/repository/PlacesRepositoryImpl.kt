package com.example.photoplaces.data.repository

import androidx.lifecycle.LiveData
import com.example.photoplaces.data.db.PlacesDao
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.data.network.RemoteDataSourceInterface
import com.example.photoplaces.data.network.response.PlacesApiResponse
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

const val STATUS_OK = "ok"

class PlacesRepositoryImpl(
    private val remoteDataSourceInterface: RemoteDataSourceInterface,
    private val placesDao: PlacesDao) : PlacesRepository, CoroutineScope  {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    init {
        remoteDataSourceInterface.downloadedPlaces.observeForever { placesResponse ->
            persistFetchedPlaces(placesResponse)
        }
    }

    private fun persistFetchedPlaces(placesResponse: PlacesApiResponse) {

        launch {
            if (placesResponse.status == STATUS_OK && placesResponse.locations.isNotEmpty()) {
                placesDao.addAllPlaces(placesResponse.locations)
            }
        }
    }

    override suspend fun fetchAllPlaces(): LiveData<List<Place>> {
        return withContext(Dispatchers.Main) {
            initPlacesData()
            placesDao.fetchAllPlaces()
        }
    }

    override suspend fun downloadingStatus(): LiveData<Boolean> {
        return withContext(Dispatchers.IO) { remoteDataSourceInterface.downloading }
    }

    override suspend fun insertOrUpdatePlace(place: Place): Place? {
        return withContext(Dispatchers.IO) {
            placesDao.insertOrUpdatePlace(place)
        }
    }

    private suspend fun initPlacesData() {
        if (placesDao.isDatabaseEmpty()) {
            remoteDataSourceInterface.fetchAllPlaces()
        }
    }

    override fun cancel() {
        job.cancel()
    }

}