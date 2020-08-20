package com.example.photoplaces.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(
    val address: String,
    val image: String?,
    val label: String,
    val lat: Double,
    val lng: Double
): Parcelable
