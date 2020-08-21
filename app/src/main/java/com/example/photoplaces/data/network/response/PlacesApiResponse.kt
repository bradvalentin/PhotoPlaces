package com.example.photoplaces.data.network.response

import com.example.photoplaces.data.entity.Place
import java.util.ArrayList

data class PlacesApiResponse(
    val locations: ArrayList<Place>,
    val status: String
)