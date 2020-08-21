package com.example.photoplaces.ui.places

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.photoplaces.data.repository.PlacesRepository

class PlacesViewModelFactory(
    private val placesRepository: PlacesRepository): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlacesViewModel(placesRepository) as T
    }
}