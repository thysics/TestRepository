package com.example.glutenfreefinderapp.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.glutenfreefinderapp.model.Restaurant
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the restaurants table
 */
@Dao
interface RestaurantDao {
    /**
     * Insert a restaurant into the database
     * If there's a conflict, replace the existing entry
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: Restaurant)
    
    /**
     * Insert multiple restaurants into the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurants(restaurants: List<Restaurant>)
    
    /**
     * Get a restaurant by its ID
     */
    @Query("SELECT * FROM restaurants WHERE id = :id")
    fun getRestaurantById(id: String): Flow<Restaurant?>
    
    /**
     * Get all restaurants
     */
    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): Flow<List<Restaurant>>
    
    /**
     * Get all gluten-free restaurants
     */
    @Query("SELECT * FROM restaurants WHERE isGlutenFree = 1")
    fun getGlutenFreeRestaurants(): Flow<List<Restaurant>>
    
    /**
     * Update a restaurant's gluten-free status
     */
    @Query("UPDATE restaurants SET isGlutenFree = :isGlutenFree, glutenFreeReviewCount = :glutenFreeCount, celiacReviewCount = :celiacCount WHERE id = :id")
    suspend fun updateGlutenFreeStatus(id: String, isGlutenFree: Boolean, glutenFreeCount: Int, celiacCount: Int)
    
    /**
     * Delete a restaurant by its ID
     */
    @Query("DELETE FROM restaurants WHERE id = :id")
    suspend fun deleteRestaurant(id: String)
    
    /**
     * Delete all restaurants
     */
    @Query("DELETE FROM restaurants")
    suspend fun deleteAllRestaurants()
}