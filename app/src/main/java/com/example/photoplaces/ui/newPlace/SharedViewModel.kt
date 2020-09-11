package com.example.photoplaces.ui.newPlace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photoplaces.data.entity.Place

class SharedViewModel : ViewModel() {

    val newPlaceMutableLiveData: LiveData<Place> = MutableLiveData<Place>()

    fun setNewPlace(place: Place) {
        (newPlaceMutableLiveData as MutableLiveData).value = place
    }

}