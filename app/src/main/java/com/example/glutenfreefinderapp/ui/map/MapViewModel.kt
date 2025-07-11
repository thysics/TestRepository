package com.example.glutenfreefinderapp.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.glutenfreefinderapp.data.repository.RestaurantRepository
import com.example.glutenfreefinderapp.model.Restaurant
import kotlinx.coroutines.launch

/**
 * ViewModel for the Map screen
 */
class MapViewModel(private val repository: RestaurantRepository) : ViewModel() {
    
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
 * Factory for creating MapViewModel with dependency
 */
class MapViewModelFactory(private val repository: RestaurantRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}