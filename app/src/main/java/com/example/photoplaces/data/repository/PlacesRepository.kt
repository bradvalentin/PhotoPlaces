package com.example.photoplaces.data.repository

import androidx.lifecycle.LiveData
import com.example.photoplaces.data.entity.CurrentLocation
import com.example.photoplaces.data.entity.Place
import io.realm.RealmResults

interface PlacesRepository {
    suspend fun fetchAllPlaces(): LiveData<List<Place>>
    suspend fun downloadingStatus(): LiveData<Boolean>
    suspend fun insertOrUpdatePlace(place: Place): Place?
}