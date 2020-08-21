package com.example.photoplaces.ui.places

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.data.repository.PlacesRepository

class PlacesViewModel(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    suspend fun getAllPlaces(): LiveData<List<Place>> {
        return placesRepository.fetchAllPlaces()
    }

    suspend fun getDownloadingStatus(): LiveData<Boolean> {
        return placesRepository.downloadingStatus()
    }

}