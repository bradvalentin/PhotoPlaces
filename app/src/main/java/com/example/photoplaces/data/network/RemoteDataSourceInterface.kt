package com.example.photoplaces.data.network

import androidx.lifecycle.LiveData
import com.example.photoplaces.data.network.response.PlacesApiResponse

interface RemoteDataSourceInterface {
    val downloadedPlaces: LiveData<PlacesApiResponse>
    val downloading: LiveData<Boolean>
    suspend fun fetchAllPlaces()
}