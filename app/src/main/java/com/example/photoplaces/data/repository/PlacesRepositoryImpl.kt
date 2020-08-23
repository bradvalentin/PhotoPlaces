package com.example.photoplaces.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photoplaces.data.db.PlacesDao
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.data.network.RemoteDataSourceInterface
import com.example.photoplaces.data.network.response.PlacesApiResponse
import io.realm.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlacesRepositoryImpl(
    private val remoteDataSourceInterface: RemoteDataSourceInterface,
    private val placesDao: PlacesDao) : PlacesRepository {

    init {
        remoteDataSourceInterface.downloadedPlaces.observeForever { placesResponse ->
            persistFetchedPlaces(placesResponse)
        }
    }

    private fun persistFetchedPlaces(placesResponse: PlacesApiResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            if (placesResponse.status == "ok" && placesResponse.locations.isNotEmpty()) {
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

}