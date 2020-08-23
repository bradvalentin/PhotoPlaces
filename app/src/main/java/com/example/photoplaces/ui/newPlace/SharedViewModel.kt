package com.example.photoplaces.ui.newPlace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photoplaces.data.entity.Place

class SharedViewModel: ViewModel() {

    private val _newPlaceMutableLiveData = MutableLiveData<Place>()
    val newPlaceMutableLiveData: LiveData<Place>
        get() = _newPlaceMutableLiveData

    fun setNewPlace(place: Place) {
        _newPlaceMutableLiveData.value = place
    }

}