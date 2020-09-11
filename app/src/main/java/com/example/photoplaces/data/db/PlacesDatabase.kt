package com.example.photoplaces.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.photoplaces.data.entity.Place

@Database(entities = [Place::class], version = 1, exportSchema = false)
abstract class PlacesDatabase : RoomDatabase() {

    abstract fun placesDao(): PlacesDao

}