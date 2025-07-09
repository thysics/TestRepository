package com.example.glutenfreefinderapp.service

import android.content.Context
import com.example.glutenfreefinderapp.model.Restaurant
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Service to search for restaurants using Google Places API
 */
class RestaurantSearchService(context: Context) {
    
    private val placesClient: PlacesClient
    private val locationService = LocationService(context)
    
    init {
        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(context, context.resources.getString(
                context.resources.getIdentifier("google_maps_api_key", "string", context.packageName)
            ))
        }
        placesClient = Places.createClient(context)
    }
    
    /**
     * Search for restaurants near a specific location
     */
    suspend fun searchNearbyRestaurants(location: LatLng, radius: Double = 5000.0): List<Restaurant> {
        // Create bounds for the search area (approximately within radius)
        val bounds = createBoundsFromLocation(location, radius)
        
        // First find restaurant predictions
        val restaurantIds = findRestaurantPredictions(bounds)
        
        // Then fetch details and reviews for each restaurant
        return restaurantIds.mapNotNull { placeId ->
            try {
                getRestaurantDetails(placeId, location)
            } catch (e: Exception) {
                null
            }
        }.filter { it.isGlutenFreeFriendly() }
    }
    
    /**
     * Find restaurant predictions within the given bounds
     */
    private suspend fun findRestaurantPredictions(bounds: RectangularBounds): List<String> = 
        suspendCancellableCoroutine { continuation ->
            val request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
                .setTypesFilter(listOf("restaurant"))
                .build()
                
            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    val placeIds = response.autocompletePredictions.map { it.placeId }
                    continuation.resume(placeIds)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    
    /**
     * Get detailed information about a restaurant including reviews
     */
    private suspend fun getRestaurantDetails(placeId: String, userLocation: LatLng): Restaurant = 
        suspendCancellableCoroutine { continuation ->
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL,
                Place.Field.REVIEWS
            )
            
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
            
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response ->
                    val place = response.place
                    
                    // Extract reviews and check for gluten-free and celiac mentions
                    val reviews = place.reviews ?: emptyList()
                    val reviewTexts = reviews.map { it.text ?: "" }
                    
                    val hasGlutenFreeReviews = reviewTexts.any { 
                        it.contains("gluten free", ignoreCase = true) || 
                        it.contains("gluten-free", ignoreCase = true) 
                    }
                    
                    val hasCeliacReviews = reviewTexts.any { 
                        it.contains("celiac", ignoreCase = true) || 
                        it.contains("coeliac", ignoreCase = true)
                    }
                    
                    // Get relevant review excerpts
                    val relevantExcerpts = reviewTexts.filter { review ->
                        review.contains("gluten", ignoreCase = true) || 
                        review.contains("celiac", ignoreCase = true) ||
                        review.contains("coeliac", ignoreCase = true)
                    }
                    
                    // Calculate distance from user
                    val distance = if (place.latLng != null) {
                        locationService.calculateDistance(userLocation, place.latLng!!)
                    } else {
                        Float.MAX_VALUE
                    }
                    
                    val restaurant = Restaurant(
                        id = place.id ?: "",
                        name = place.name ?: "",
                        address = place.address ?: "",
                        location = place.latLng ?: userLocation,
                        rating = place.rating?.toFloat() ?: 0f,
                        distance = distance,
                        hasGlutenFreeReviews = hasGlutenFreeReviews,
                        hasCeliacReviews = hasCeliacReviews,
                        reviewExcerpts = relevantExcerpts
                    )
                    
                    continuation.resume(restaurant)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    
    /**
     * Create rectangular bounds around a location with approximate radius
     */
    private fun createBoundsFromLocation(center: LatLng, radiusInMeters: Double): RectangularBounds {
        val latRadian = Math.toRadians(center.latitude)
        
        // Approximate degrees per meter
        val latRadiusInDegrees = (radiusInMeters / 111320.0)
        val lngRadiusInDegrees = (radiusInMeters / (111320.0 * Math.cos(latRadian)))
        
        val southwest = LatLng(
            center.latitude - latRadiusInDegrees,
            center.longitude - lngRadiusInDegrees
        )
        
        val northeast = LatLng(
            center.latitude + latRadiusInDegrees,
            center.longitude + lngRadiusInDegrees
        )
        
        return RectangularBounds.newInstance(southwest, northeast)
    }
}