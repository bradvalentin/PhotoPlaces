package com.example.photoplaces.data.network

import androidx.lifecycle.LiveData
import com.example.photoplaces.data.network.response.PlacesApiResponse

interface RemoteDataSourceInterface {
    var downloadedPlaces: LiveData<PlacesApiResponse>
    var downloading: LiveData<Boolean>
    suspend fun fetchAllPlaces()
}