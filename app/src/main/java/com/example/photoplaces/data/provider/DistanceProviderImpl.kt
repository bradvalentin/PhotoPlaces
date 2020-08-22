package com.example.photoplaces.data.provider

import android.location.Location
import com.example.photoplaces.data.entity.CurrentLocation
import com.example.photoplaces.data.entity.Place

class DistanceProviderImpl() : DistanceProvider {
    override fun distanceBetweenTwoLocations(place: Place, location: CurrentLocation): Float {
        val locationA = Location("pointA")
        locationA.apply {
            latitude = place.lat
            longitude = place.lng
        }
        val locationB = Location("pointB")
        locationB.apply {
            latitude = location.latitude
            longitude = location.longitude
        }
        return locationA.distanceTo(locationB)
    }
}