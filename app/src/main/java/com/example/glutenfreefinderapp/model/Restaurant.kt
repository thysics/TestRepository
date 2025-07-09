package com.example.glutenfreefinderapp.model

import com.google.android.gms.maps.model.LatLng

/**
 * Data class representing a restaurant with gluten-free options
 */
data class Restaurant(
    val id: String,
    val name: String,
    val address: String,
    val location: LatLng,
    val rating: Float,
    val distance: Float, // in meters
    val hasGlutenFreeReviews: Boolean = false,
    val hasCeliacReviews: Boolean = false,
    val reviewExcerpts: List<String> = emptyList()
) {
    /**
     * Determines if the restaurant meets the criteria for being gluten-free friendly
     * Must have both "gluten free" and "celiac" mentioned in reviews
     */
    fun isGlutenFreeFriendly(): Boolean {
        return hasGlutenFreeReviews && hasCeliacReviews
    }
    
    /**
     * Returns the distance in miles (for display purposes)
     */
    fun getDistanceInMiles(): Float {
        return distance / 1609.34f
    }
}