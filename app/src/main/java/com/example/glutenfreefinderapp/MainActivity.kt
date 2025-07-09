package com.example.glutenfreefinderapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.glutenfreefinderapp.adapter.RestaurantAdapter
import com.example.glutenfreefinderapp.databinding.ActivityMainBinding
import com.example.glutenfreefinderapp.model.Restaurant
import com.example.glutenfreefinderapp.service.LocationService
import com.example.glutenfreefinderapp.service.RestaurantSearchService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var locationService: LocationService
    private lateinit var restaurantSearchService: RestaurantSearchService
    private lateinit var restaurantAdapter: RestaurantAdapter
    
    private var currentLocation: LatLng? = null
    private var restaurants: List<Restaurant> = emptyList()
    
    // Permission request launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions.entries.all { it.value }
        if (locationGranted) {
            getUserLocationAndSearchRestaurants()
        } else {
            Toast.makeText(
                this,
                R.string.location_permission_required,
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize services
        locationService = LocationService(this)
        restaurantSearchService = RestaurantSearchService(this)
        
        // Set up map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        // Set up RecyclerView
        setupRecyclerView()
        
        // Set up search button
        binding.searchButton.setOnClickListener {
            if (currentLocation != null) {
                searchGlutenFreeRestaurants(currentLocation!!)
            } else {
                checkLocationPermissionAndGetLocation()
            }
        }
        
        // Set up current location button
        binding.currentLocationButton.setOnClickListener {
            checkLocationPermissionAndGetLocation()
        }
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Check location permission and get user location
        checkLocationPermissionAndGetLocation()
        
        // Set up map click listener
        googleMap.setOnMarkerClickListener { marker ->
            val restaurant = restaurants.find { it.id == marker.tag }
            restaurant?.let {
                // Show restaurant details
                showRestaurantDetails(it)
            }
            true
        }
    }
    
    private fun setupRecyclerView() {
        restaurantAdapter = RestaurantAdapter { restaurant ->
            // Handle restaurant item click
            showRestaurantDetails(restaurant)
        }
        
        binding.restaurantsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = restaurantAdapter
        }
    }
    
    private fun checkLocationPermissionAndGetLocation() {
        if (locationService.hasLocationPermission()) {
            getUserLocationAndSearchRestaurants()
        } else {
            requestLocationPermission()
        }
    }
    
    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    
    private fun getUserLocationAndSearchRestaurants() {
        lifecycleScope.launch {
            try {
                // Show loading indicator
                binding.progressBar.visibility = View.VISIBLE
                
                // Get user location
                val location = locationService.getUserLocation()
                currentLocation = location
                
                // Move camera to user location
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(location, 14f)
                )
                
                // Search for gluten-free restaurants
                searchGlutenFreeRestaurants(location)
                
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    R.string.error_occurred,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun searchGlutenFreeRestaurants(location: LatLng) {
        lifecycleScope.launch {
            try {
                // Show loading indicator
                binding.progressBar.visibility = View.VISIBLE
                
                // Search for restaurants
                restaurants = restaurantSearchService.searchNearbyRestaurants(location)
                
                // Update UI with results
                updateRestaurantsList(restaurants)
                updateMapMarkers(restaurants)
                
                // Show results or no results message
                if (restaurants.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        R.string.no_results,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    R.string.error_occurred,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun updateRestaurantsList(restaurants: List<Restaurant>) {
        restaurantAdapter.submitList(restaurants)
        binding.restaurantsRecyclerView.visibility = 
            if (restaurants.isNotEmpty()) View.VISIBLE else View.GONE
    }
    
    private fun updateMapMarkers(restaurants: List<Restaurant>) {
        // Clear existing markers
        googleMap.clear()
        
        // Add markers for each restaurant
        restaurants.forEach { restaurant ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(restaurant.location)
                    .title(restaurant.name)
            )
            
            // Set restaurant ID as marker tag for identification
            marker?.tag = restaurant.id
        }
    }
    
    private fun showRestaurantDetails(restaurant: Restaurant) {
        // Move camera to restaurant location
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(restaurant.location, 16f)
        )
        
        // TODO: Show more detailed information in a bottom sheet or dialog
        Toast.makeText(
            this,
            "${restaurant.name} - Gluten-Free & Celiac Friendly",
            Toast.LENGTH_SHORT
        ).show()
    }
}