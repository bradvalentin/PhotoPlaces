package com.example.photoplaces.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.photoplaces.data.entity.Place

@Dao
interface PlacesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    fun addAllPlaces(places: List<Place>)

    @Query("SELECT * FROM Places")
    fun fetchAllPlaces(): LiveData<List<Place>>

    @Query("SELECT COUNT(*) FROM Places")
    fun isDatabaseEmpty(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdatePlace(place: Place)
}