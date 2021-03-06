package com.example.photoplaces.data.repository

import androidx.lifecycle.LiveData
import com.example.photoplaces.data.entity.Place

interface PlacesRepository {
    suspend fun fetchAllPlaces(): LiveData<List<Place>>
    suspend fun downloadingStatus(): LiveData<Boolean>
    suspend fun insertOrUpdatePlace(place: Place): Place?
    fun cancel()

}