package com.example.photoplaces.data.provider

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.photoplaces.data.entity.CurrentLocation

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    val locationData: LiveData<CurrentLocation> = LocationLiveData(application)
}