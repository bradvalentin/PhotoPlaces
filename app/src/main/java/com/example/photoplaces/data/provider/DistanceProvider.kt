package com.example.photoplaces.data.provider

import com.example.photoplaces.data.entity.CurrentLocation
import com.example.photoplaces.data.entity.Place

interface DistanceProvider {
    fun distanceBetweenTwoLocations(
        place: Place,
        location: CurrentLocation
    ): Float
}