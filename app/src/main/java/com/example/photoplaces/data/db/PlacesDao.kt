package com.example.photoplaces.data.db

import androidx.lifecycle.LiveData
import com.example.photoplaces.data.entity.Place
import io.realm.RealmResults

interface PlacesDao {
    fun addAllPlaces(places: List<Place>): Boolean
    fun fetchAllPlaces(): LiveData<List<Place>>
    fun isDatabaseEmpty(): Boolean
    fun insertOrUpdatePlace(place: Place): Place?
}