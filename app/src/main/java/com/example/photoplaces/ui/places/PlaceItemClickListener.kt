package com.example.photoplaces.ui.places

import com.example.photoplaces.data.entity.Place

interface PlaceItemClickListener {
    fun placeItemPressed(place: Place)
}