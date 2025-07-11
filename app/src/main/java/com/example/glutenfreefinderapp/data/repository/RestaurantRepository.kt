package com.example.glutenfreefinderapp.data.repository

import com.example.glutenfreefinderapp.data.source.local.RestaurantDao
import com.example.glutenfreefinderapp.data.source.remote.GoogleMapsApiService
import com.example.glutenfreefinderapp.model.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository for accessing restaurant data from local database and remote API
 */
class RestaurantRepository(
    private val restaurantDao: RestaurantDao,
    private val apiService: GoogleMapsApiService
) {
    // API key should be stored securely, this is just a placeholder
    private val apiKey = "YOUR_API_KEY"
    
    /**
     * Get all restaurants from local database
     */
    fun getAllRestaurants(): Flow<List<Restaurant>> {
        return restaurantDao.getAllRestaurants()
    }
    
    /**
     * Get all gluten-free restaurants from local database
     */
    fun getGlutenFreeRestaurants(): Flow<List<Restaurant>> {
        return restaurantDao.getGlutenFreeRestaurants()
    }
    
    /**
     * Get a restaurant by ID from local database
     */
    fun getRestaurantById(id: String): Flow<Restaurant?> {
        return restaurantDao.getRestaurantById(id)
    }
    
    /**
     * Search for nearby restaurants and check if they are gluten-free
     * @param latitude Current latitude
     * @param longitude Current longitude
     * @param radius Search radius in meters
     */
    suspend fun searchNearbyGlutenFreeRestaurants(
        latitude: Double,
        longitude: Double,
        radius: Int = 5000
    ) {
        withContext(Dispatchers.IO) {
            try {
                // Search for nearby restaurants
                val location = "$latitude,$longitude"
                val response = apiService.getNearbyRestaurants(
                    location = location,
                    radius = radius,
                    type = "restaurant",
                    key = apiKey
                )
                
                // Process each restaurant
                response.results.forEach { place ->
                    // Get details including reviews
                    val detailsResponse = apiService.getPlaceDetails(
                        placeId = place.placeId,
                        key = apiKey
                    )
                    
                    // Process reviews to check for gluten-free and celiac mentions
                    var glutenFreeCount = 0
                    var celiacCount = 0
                    
                    detailsResponse.result.reviews?.forEach { review ->
                        val reviewText = review.text.lowercase()
                        if (reviewText.contains("gluten free") || reviewText.contains("gluten-free")) {
                            glutenFreeCount++
                        }
                        if (reviewText.contains("celiac") || reviewText.contains("coeliac")) {
                            celiacCount++
                        }
                    }
                    
                    // Create restaurant object
                    val isGlutenFree = glutenFreeCount > 0 && celiacCount > 0
                    val restaurant = Restaurant(
                        id = place.placeId,
                        name = place.name,
                        address = place.vicinity,
                        latitude = place.geometry.location.lat,
                        longitude = place.geometry.location.lng,
                        rating = place.rating ?: 0f,
                        totalReviews = place.userRatingsTotal ?: 0,
                        isGlutenFree = isGlutenFree,
                        glutenFreeReviewCount = glutenFreeCount,
                        celiacReviewCount = celiacCount,
                        phoneNumber = detailsResponse.result.formattedPhoneNumber,
                        website = detailsResponse.result.website,
                        openingHours = detailsResponse.result.openingHours?.weekdayText?.joinToString("\n"),
                        priceLevel = detailsResponse.result.priceLevel,
                        photoUrl = detailsResponse.result.photos?.firstOrNull()?.let {
                            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${it.photoReference}&key=$apiKey"
                        }
                    )
                    
                    // Save to database
                    restaurantDao.insertRestaurant(restaurant)
                }
            } catch (e: Exception) {
                // Handle errors
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Update a restaurant's gluten-free status
     */
    suspend fun updateGlutenFreeStatus(
        id: String,
        isGlutenFree: Boolean,
        glutenFreeCount: Int,
        celiacCount: Int
    ) {
        restaurantDao.updateGlutenFreeStatus(id, isGlutenFree, glutenFreeCount, celiacCount)
    }
    
    /**
     * Delete all restaurants from local database
     */
    suspend fun clearAllRestaurants() {
        restaurantDao.deleteAllRestaurants()
    }
}