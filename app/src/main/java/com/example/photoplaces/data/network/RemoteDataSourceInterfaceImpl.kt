package com.example.photoplaces.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photoplaces.data.network.api.PlacesApiService
import com.example.photoplaces.data.network.response.PlacesApiResponse
import com.example.photoplaces.utils.NoConnectivityException

class RemoteDataSourceInterfaceImpl(
    private val placesApiService: PlacesApiService
) : RemoteDataSourceInterface {

    private val _downloadedPlaces = MutableLiveData<PlacesApiResponse>()
    override val downloadedPlaces: LiveData<PlacesApiResponse>
        get() = _downloadedPlaces

    private val _downloading = MutableLiveData<Boolean>()
    override val downloading: LiveData<Boolean>
        get() = _downloading

    override suspend fun fetchAllPlaces() {
        try {
            val fetchedPlaces = placesApiService.fetchAllPlaces()

            _downloadedPlaces.postValue(fetchedPlaces.body())
            _downloading.postValue(true)

        } catch (e: NoConnectivityException) {
            Log.e("Connection", "No internet", e)
            _downloading.postValue(false)

        }
    }

}
