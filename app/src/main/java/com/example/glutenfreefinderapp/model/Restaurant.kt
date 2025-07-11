package com.example.glutenfreefinderapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Restaurant entity representing a gluten-free restaurant
 * This class is used for Room database storage and API responses
 */
@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey
    val id: String,
    
    val name: String,
    
    val address: String,
    
    val latitude: Double,
    
    val longitude: Double,
    
    val rating: Float,
    
    val totalReviews: Int,
    
    /**
     * Indicates if the restaurant has been confirmed as gluten-free
     * This is determined by finding both "gluten free" and "celiac" in reviews
     */
    val isGlutenFree: Boolean = false,
    
    /**
     * Count of reviews mentioning "gluten free"
     */
    val glutenFreeReviewCount: Int = 0,
    
    /**
     * Count of reviews mentioning "celiac"
     */
    val celiacReviewCount: Int = 0,
    
    /**
     * Phone number of the restaurant
     */
    val phoneNumber: String? = null,
    
    /**
     * Website URL of the restaurant
     */
    val website: String? = null,
    
    /**
     * Opening hours of the restaurant in a formatted string
     */
    val openingHours: String? = null,
    
    /**
     * Price level indicator (1-4)
     */
    val priceLevel: Int? = null,
    
    /**
     * URL to the restaurant's photo
     */
    val photoUrl: String? = null
) {
    /**
     * Determines if a restaurant is considered gluten-free based on reviews
     * A restaurant is considered gluten-free if both "gluten free" and "celiac"
     * are mentioned in its reviews
     */
    fun isConfirmedGlutenFree(): Boolean {
        return glutenFreeReviewCount > 0 && celiacReviewCount > 0
    }
    
    /**
     * Returns the distance to the restaurant from the current location
     * @param currentLat Current latitude
     * @param currentLng Current longitude
     * @return Distance in kilometers
     */
    fun distanceFrom(currentLat: Double, currentLng: Double): Double {
        val earthRadius = 6371.0 // Earth's radius in kilometers
        
        val latDistance = Math.toRadians(latitude - currentLat)
        val lngDistance = Math.toRadians(longitude - currentLng)
        
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(currentLat)) * Math.cos(Math.toRadians(latitude)) *
                Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return earthRadius * c
    }
}