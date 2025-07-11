package com.example.glutenfreefinderapp.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewTest {

    @Test
    fun `mentionsGlutenFree returns true when review contains 'gluten free'`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "This restaurant has great gluten free options!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.mentionsGlutenFree()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `mentionsGlutenFree returns true when review contains 'gluten-free'`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "This restaurant has great gluten-free options!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.mentionsGlutenFree()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `mentionsGlutenFree returns true regardless of case`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "This restaurant has great GLUTEN FREE options!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.mentionsGlutenFree()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `mentionsGlutenFree returns false when review does not contain gluten free terms`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "This restaurant has great food!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.mentionsGlutenFree()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `mentionsCeliac returns true when review contains 'celiac'`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "Great for people with celiac disease!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.mentionsCeliac()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `mentionsCeliac returns true when review contains 'coeliac'`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "Great for people with coeliac disease!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.mentionsCeliac()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `mentionsCeliac returns true when review contains 'celíaco'`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "Excelente para personas con enfermedad celíaco!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.mentionsCeliac()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `mentionsCeliac returns true when review contains 'celiaco'`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "Excelente para personas con enfermedad celiaco!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.mentionsCeliac()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `mentionsCeliac returns true regardless of case`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "Great for people with CELIAC disease!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.mentionsCeliac()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `mentionsCeliac returns false when review does not contain celiac terms`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "This restaurant has great food!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.mentionsCeliac()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `isRelevantForGlutenFreeSearch returns true when review mentions gluten free`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "This restaurant has great gluten free options!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.isRelevantForGlutenFreeSearch()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `isRelevantForGlutenFreeSearch returns true when review mentions celiac`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "Great for people with celiac disease!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.isRelevantForGlutenFreeSearch()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `isRelevantForGlutenFreeSearch returns true when review mentions both gluten free and celiac`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "This restaurant has great gluten free options for people with celiac disease!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.isRelevantForGlutenFreeSearch()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `isRelevantForGlutenFreeSearch returns false when review mentions neither gluten free nor celiac`() {
        // Given
        val review = Review(
            id = "review1",
            restaurantId = "restaurant1",
            authorName = "John Doe",
            rating = 4.5f,
            text = "This restaurant has great food!",
            time = 1625097600000,
            relativeTimeDescription = "1 month ago"
        )
        
        // When
        val result = review.isRelevantForGlutenFreeSearch()
        
        // Then
        assertFalse(result)
    }
}