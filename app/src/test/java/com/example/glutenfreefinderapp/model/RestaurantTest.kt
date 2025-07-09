package com.example.glutenfreefinderapp.model

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RestaurantTest {
    
    @Test
    fun `isGlutenFreeFriendly returns true when both gluten free and celiac reviews exist`() {
        // Arrange
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            location = LatLng(0.0, 0.0),
            rating = 4.5f,
            distance = 1000f,
            hasGlutenFreeReviews = true,
            hasCeliacReviews = true
        )
        
        // Act & Assert
        assertTrue(restaurant.isGlutenFreeFriendly())
    }
    
    @Test
    fun `isGlutenFreeFriendly returns false when only gluten free reviews exist`() {
        // Arrange
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            location = LatLng(0.0, 0.0),
            rating = 4.5f,
            distance = 1000f,
            hasGlutenFreeReviews = true,
            hasCeliacReviews = false
        )
        
        // Act & Assert
        assertFalse(restaurant.isGlutenFreeFriendly())
    }
    
    @Test
    fun `isGlutenFreeFriendly returns false when only celiac reviews exist`() {
        // Arrange
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            location = LatLng(0.0, 0.0),
            rating = 4.5f,
            distance = 1000f,
            hasGlutenFreeReviews = false,
            hasCeliacReviews = true
        )
        
        // Act & Assert
        assertFalse(restaurant.isGlutenFreeFriendly())
    }
    
    @Test
    fun `isGlutenFreeFriendly returns false when neither gluten free nor celiac reviews exist`() {
        // Arrange
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            location = LatLng(0.0, 0.0),
            rating = 4.5f,
            distance = 1000f,
            hasGlutenFreeReviews = false,
            hasCeliacReviews = false
        )
        
        // Act & Assert
        assertFalse(restaurant.isGlutenFreeFriendly())
    }
    
    @Test
    fun `getDistanceInMiles converts meters to miles correctly`() {
        // Arrange
        val distanceInMeters = 1609.34f // approximately 1 mile
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            location = LatLng(0.0, 0.0),
            rating = 4.5f,
            distance = distanceInMeters
        )
        
        // Act
        val distanceInMiles = restaurant.getDistanceInMiles()
        
        // Assert
        assertEquals(1.0f, distanceInMiles, 0.01f)
    }
}