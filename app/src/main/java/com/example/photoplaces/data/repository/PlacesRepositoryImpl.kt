package com.example.photoplaces.data.repository

import androidx.lifecycle.LiveData
import com.example.photoplaces.data.network.RemoteDataSourceInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlacesRepositoryImpl(
    private val remoteDataSourceInterface: RemoteDataSourceInterface
) : PlacesRepository {

    init {
        remoteDataSourceInterface.downloadedPlaces.observeForever { places ->
        }
    }

    override suspend fun fetchAllPlaces() {
        return withContext(Dispatchers.IO) { initWeatherData() }
    }

    override suspend fun downloadingStatus(): LiveData<Boolean> {
        return withContext(Dispatchers.IO) { remoteDataSourceInterface.downloading }
    }

    private suspend fun initWeatherData() {
        remoteDataSourceInterface.fetchAllPlaces()
    }

}