package com.example.glutenfreefinderapp.model

/**
 * Represents a review for a restaurant
 */
data class Review(
    val id: String,
    val restaurantId: String,
    val authorName: String,
    val rating: Float,
    val text: String,
    val time: Long,
    val relativeTimeDescription: String
) {
    /**
     * Checks if the review mentions gluten-free options
     */
    fun mentionsGlutenFree(): Boolean {
        return text.lowercase().contains("gluten free") || 
               text.lowercase().contains("gluten-free")
    }
    
    /**
     * Checks if the review mentions celiac disease
     */
    fun mentionsCeliac(): Boolean {
        return text.lowercase().contains("celiac") || 
               text.lowercase().contains("coeliac") ||
               text.lowercase().contains("celíaco") ||
               text.lowercase().contains("celiaco")
    }
    
    /**
     * Checks if the review is relevant for gluten-free search
     * A review is relevant if it mentions either gluten-free or celiac
     */
    fun isRelevantForGlutenFreeSearch(): Boolean {
        return mentionsGlutenFree() || mentionsCeliac()
    }
}