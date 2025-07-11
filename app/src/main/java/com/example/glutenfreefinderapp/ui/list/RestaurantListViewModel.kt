package com.example.glutenfreefinderapp.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.glutenfreefinderapp.data.repository.RestaurantRepository
import com.example.glutenfreefinderapp.model.Restaurant
import kotlinx.coroutines.launch

/**
 * ViewModel for the Restaurant List screen
 */
class RestaurantListViewModel(private val repository: RestaurantRepository) : ViewModel() {
    
    // LiveData for all gluten-free restaurants
    val glutenFreeRestaurants: LiveData<List<Restaurant>> = repository.getGlutenFreeRestaurants().asLiveData()
    
    /**
     * Search for nearby gluten-free restaurants
     */
    fun searchNearbyGlutenFreeRestaurants(latitude: Double, longitude: Double, radius: Int = 5000) {
        viewModelScope.launch {
            repository.searchNearbyGlutenFreeRestaurants(latitude, longitude, radius)
        }
    }
}

/**
 * Factory for creating RestaurantListViewModel with dependency
 */
class RestaurantListViewModelFactory(private val repository: RestaurantRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RestaurantListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}