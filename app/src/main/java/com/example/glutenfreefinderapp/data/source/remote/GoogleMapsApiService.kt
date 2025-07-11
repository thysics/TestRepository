package com.example.glutenfreefinderapp.data.source.remote

import com.example.glutenfreefinderapp.data.source.remote.response.NearbySearchResponse
import com.example.glutenfreefinderapp.data.source.remote.response.PlaceDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for Google Maps API
 */
interface GoogleMapsApiService {
    
    /**
     * Get nearby restaurants
     * @param location Latitude and longitude in format "lat,lng"
     * @param radius Search radius in meters
     * @param type Type of place (restaurant)
     * @param keyword Optional keyword to filter results
     * @param key Google Maps API key
     */
    @GET("place/nearbysearch/json")
    suspend fun getNearbyRestaurants(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String = "restaurant",
        @Query("keyword") keyword: String? = null,
        @Query("key") key: String
    ): NearbySearchResponse
    
    /**
     * Get details of a specific place including reviews
     * @param placeId The ID of the place
     * @param fields Fields to return (name, rating, reviews, etc.)
     * @param key Google Maps API key
     */
    @GET("place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String = "name,rating,formatted_address,geometry,reviews,website,formatted_phone_number,opening_hours,price_level,photos",
        @Query("key") key: String
    ): PlaceDetailsResponse
}