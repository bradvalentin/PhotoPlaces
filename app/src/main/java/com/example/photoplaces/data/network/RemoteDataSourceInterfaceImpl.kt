package com.example.photoplaces.data.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photoplaces.data.network.api.PlacesApiService
import com.example.photoplaces.data.network.response.PlacesApiResponse
import com.example.photoplaces.utils.NoConnectivityException

class RemoteDataSourceInterfaceImpl(
    private val placesApiService: PlacesApiService
) : RemoteDataSourceInterface {

    override var downloadedPlaces: LiveData<PlacesApiResponse> =
        MutableLiveData<PlacesApiResponse>()

    private fun setDownloadedPlaces(placesApiResponse: PlacesApiResponse?) {
        (downloadedPlaces as MutableLiveData).value = placesApiResponse
    }

    override var downloading: LiveData<Boolean> = MutableLiveData<Boolean>()
    private fun setDownloading(isDownloading: Boolean) {
        (downloading as MutableLiveData).value = isDownloading
    }

    override suspend fun fetchAllPlaces() {
        try {
            val fetchedPlaces = placesApiService.fetchAllPlaces()

            setDownloadedPlaces(fetchedPlaces.body())
            setDownloading(true)

        } catch (e: NoConnectivityException) {
            setDownloading(false)
        }
    }

}
