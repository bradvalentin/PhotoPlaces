package com.example.photoplaces.data.repository

import androidx.lifecycle.LiveData

interface PlacesRepository {
    suspend fun fetchAllPlaces()
    suspend fun downloadingStatus(): LiveData<Boolean>

}