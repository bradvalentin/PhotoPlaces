package com.example.photoplaces.data.network.api

import com.example.photoplaces.data.network.response.PlacesApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface PlacesApiService {

    @GET("mylocations")
    suspend fun fetchAllPlaces(): Response<PlacesApiResponse>
}