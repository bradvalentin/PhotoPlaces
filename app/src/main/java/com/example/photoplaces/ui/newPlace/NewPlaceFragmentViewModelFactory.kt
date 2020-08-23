package com.example.photoplaces.ui.newPlace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.photoplaces.data.repository.PlacesRepository

class NewPlaceFragmentViewModelFactory(
    private val placesRepository: PlacesRepository
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewPlaceFragmentViewModel(
            placesRepository
        ) as T
    }
}