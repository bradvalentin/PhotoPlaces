package com.example.photoplaces.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "Places")
@Parcelize
data class Place(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var address: String = "",
    var image: String? = null,
    var label: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    @Ignore
    var distance: String? = null
): Parcelable
