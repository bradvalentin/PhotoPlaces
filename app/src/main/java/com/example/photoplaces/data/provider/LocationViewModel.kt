package com.example.photoplaces.data.provider

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.photoplaces.data.entity.CurrentLocation
import com.example.photoplaces.data.provider.LocationLiveData

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val _locationData =
        LocationLiveData(application)
    val locationData: LiveData<CurrentLocation> = _locationData
}