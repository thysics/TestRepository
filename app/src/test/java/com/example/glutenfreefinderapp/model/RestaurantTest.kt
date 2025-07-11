package com.example.glutenfreefinderapp.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class RestaurantTest {

    @Test
    fun `isConfirmedGlutenFree returns true when both glutenFree and celiac reviews exist`() {
        // Given
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            latitude = 37.7749,
            longitude = -122.4194,
            rating = 4.5f,
            totalReviews = 100,
            glutenFreeReviewCount = 5,
            celiacReviewCount = 3
        )
        
        // When
        val result = restaurant.isConfirmedGlutenFree()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `isConfirmedGlutenFree returns false when only glutenFree reviews exist`() {
        // Given
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            latitude = 37.7749,
            longitude = -122.4194,
            rating = 4.5f,
            totalReviews = 100,
            glutenFreeReviewCount = 5,
            celiacReviewCount = 0
        )
        
        // When
        val result = restaurant.isConfirmedGlutenFree()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `isConfirmedGlutenFree returns false when only celiac reviews exist`() {
        // Given
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            latitude = 37.7749,
            longitude = -122.4194,
            rating = 4.5f,
            totalReviews = 100,
            glutenFreeReviewCount = 0,
            celiacReviewCount = 3
        )
        
        // When
        val result = restaurant.isConfirmedGlutenFree()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `isConfirmedGlutenFree returns false when no glutenFree or celiac reviews exist`() {
        // Given
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            latitude = 37.7749,
            longitude = -122.4194,
            rating = 4.5f,
            totalReviews = 100,
            glutenFreeReviewCount = 0,
            celiacReviewCount = 0
        )
        
        // When
        val result = restaurant.isConfirmedGlutenFree()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `distanceFrom calculates correct distance between two points`() {
        // Given
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            latitude = 37.7749,
            longitude = -122.4194,
            rating = 4.5f,
            totalReviews = 100
        )
        
        val currentLat = 37.7833
        val currentLng = -122.4167
        
        // Expected distance is approximately 1.13 km
        val expectedDistance = 1.13
        
        // When
        val calculatedDistance = restaurant.distanceFrom(currentLat, currentLng)
        
        // Then
        // Allow for small floating point differences
        assertTrue(abs(calculatedDistance - expectedDistance) < 0.1)
    }
    
    @Test
    fun `distanceFrom returns zero for same coordinates`() {
        // Given
        val lat = 37.7749
        val lng = -122.4194
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            latitude = lat,
            longitude = lng,
            rating = 4.5f,
            totalReviews = 100
        )
        
        // When
        val calculatedDistance = restaurant.distanceFrom(lat, lng)
        
        // Then
        assertEquals(0.0, calculatedDistance, 0.0001)
    }
    
    @Test
    fun `constructor sets default values correctly`() {
        // Given
        val id = "test_id"
        val name = "Test Restaurant"
        val address = "123 Test St"
        val latitude = 37.7749
        val longitude = -122.4194
        val rating = 4.5f
        val totalReviews = 100
        
        // When
        val restaurant = Restaurant(
            id = id,
            name = name,
            address = address,
            latitude = latitude,
            longitude = longitude,
            rating = rating,
            totalReviews = totalReviews
        )
        
        // Then
        assertEquals(id, restaurant.id)
        assertEquals(name, restaurant.name)
        assertEquals(address, restaurant.address)
        assertEquals(latitude, restaurant.latitude, 0.0)
        assertEquals(longitude, restaurant.longitude, 0.0)
        assertEquals(rating, restaurant.rating)
        assertEquals(totalReviews, restaurant.totalReviews)
        assertFalse(restaurant.isGlutenFree)
        assertEquals(0, restaurant.glutenFreeReviewCount)
        assertEquals(0, restaurant.celiacReviewCount)
        assertEquals(null, restaurant.phoneNumber)
        assertEquals(null, restaurant.website)
        assertEquals(null, restaurant.openingHours)
        assertEquals(null, restaurant.priceLevel)
        assertEquals(null, restaurant.photoUrl)
    }
    
    @Test
    fun `constructor sets all values correctly`() {
        // Given
        val id = "test_id"
        val name = "Test Restaurant"
        val address = "123 Test St"
        val latitude = 37.7749
        val longitude = -122.4194
        val rating = 4.5f
        val totalReviews = 100
        val isGlutenFree = true
        val glutenFreeReviewCount = 5
        val celiacReviewCount = 3
        val phoneNumber = "123-456-7890"
        val website = "https://example.com"
        val openingHours = "Mon-Fri: 9am-5pm"
        val priceLevel = 2
        val photoUrl = "https://example.com/photo.jpg"
        
        // When
        val restaurant = Restaurant(
            id = id,
            name = name,
            address = address,
            latitude = latitude,
            longitude = longitude,
            rating = rating,
            totalReviews = totalReviews,
            isGlutenFree = isGlutenFree,
            glutenFreeReviewCount = glutenFreeReviewCount,
            celiacReviewCount = celiacReviewCount,
            phoneNumber = phoneNumber,
            website = website,
            openingHours = openingHours,
            priceLevel = priceLevel,
            photoUrl = photoUrl
        )
        
        // Then
        assertEquals(id, restaurant.id)
        assertEquals(name, restaurant.name)
        assertEquals(address, restaurant.address)
        assertEquals(latitude, restaurant.latitude, 0.0)
        assertEquals(longitude, restaurant.longitude, 0.0)
        assertEquals(rating, restaurant.rating)
        assertEquals(totalReviews, restaurant.totalReviews)
        assertEquals(isGlutenFree, restaurant.isGlutenFree)
        assertEquals(glutenFreeReviewCount, restaurant.glutenFreeReviewCount)
        assertEquals(celiacReviewCount, restaurant.celiacReviewCount)
        assertEquals(phoneNumber, restaurant.phoneNumber)
        assertEquals(website, restaurant.website)
        assertEquals(openingHours, restaurant.openingHours)
        assertEquals(priceLevel, restaurant.priceLevel)
        assertEquals(photoUrl, restaurant.photoUrl)
    }
    
    @Test
    fun `isGlutenFree property can be different from isConfirmedGlutenFree method result`() {
        // Given
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            latitude = 37.7749,
            longitude = -122.4194,
            rating = 4.5f,
            totalReviews = 100,
            isGlutenFree = true,  // Explicitly set to true
            glutenFreeReviewCount = 0,  // But no reviews
            celiacReviewCount = 0
        )
        
        // When & Then
        assertTrue(restaurant.isGlutenFree)  // Property is true
        assertFalse(restaurant.isConfirmedGlutenFree())  // Method returns false
    }
    
    @Test
    fun `distanceFrom handles antipodal points correctly`() {
        // Given
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            latitude = 90.0,  // North Pole
            longitude = 0.0,
            rating = 4.5f,
            totalReviews = 100
        )
        
        val currentLat = -90.0  // South Pole
        val currentLng = 0.0
        
        // Expected distance is approximately 20,015 km (half the Earth's circumference)
        val expectedDistance = 20015.0
        
        // When
        val calculatedDistance = restaurant.distanceFrom(currentLat, currentLng)
        
        // Then
        // Allow for larger difference due to the approximation in the distance calculation
        assertTrue(abs(calculatedDistance - expectedDistance) < 100.0)
    }
    
    @Test
    fun `distanceFrom handles equatorial points correctly`() {
        // Given
        val restaurant = Restaurant(
            id = "test_id",
            name = "Test Restaurant",
            address = "123 Test St",
            latitude = 0.0,
            longitude = 0.0,  // Prime meridian at equator
            rating = 4.5f,
            totalReviews = 100
        )
        
        val currentLat = 0.0
        val currentLng = 90.0  // 90 degrees east at equator
        
        // Expected distance is approximately 10,007 km (quarter of Earth's circumference)
        val expectedDistance = 10007.0
        
        // When
        val calculatedDistance = restaurant.distanceFrom(currentLat, currentLng)
        
        // Then
        // Allow for larger difference due to the approximation in the distance calculation
        assertTrue(abs(calculatedDistance - expectedDistance) < 100.0)
    }
}