package com.example.glutenfreefinderapp

import android.app.Application
import com.example.glutenfreefinderapp.data.repository.RestaurantRepository
import com.example.glutenfreefinderapp.data.source.local.RestaurantDatabase
import com.example.glutenfreefinderapp.data.source.remote.GoogleMapsApiService
import com.example.glutenfreefinderapp.data.source.remote.RetrofitClient

class GlutenFreeFinderApp : Application() {
    
    // Lazy initialization of the database
    val database by lazy { RestaurantDatabase.getDatabase(this) }
    
    // Lazy initialization of the API service
    val apiService by lazy { RetrofitClient.createGoogleMapsApiService() }
    
    // Repository that will be used throughout the app
    val repository by lazy { RestaurantRepository(database.restaurantDao(), apiService) }
    
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide configurations here
    }
}