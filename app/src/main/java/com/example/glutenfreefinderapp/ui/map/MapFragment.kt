package com.example.glutenfreefinderapp.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.glutenfreefinderapp.GlutenFreeFinderApp
import com.example.glutenfreefinderapp.R
import com.example.glutenfreefinderapp.databinding.FragmentMapBinding
import com.example.glutenfreefinderapp.model.Restaurant
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: MapViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        
        // Initialize ViewModel
        val repository = (requireActivity().application as GlutenFreeFinderApp).repository
        val viewModelFactory = MapViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MapViewModel::class.java]
        
        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        
        // Set up the map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        // Set up search button
        binding.btnSearch.setOnClickListener {
            getCurrentLocationAndSearch()
        }
        
        return binding.root
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Enable my location button if permission is granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
        }
        
        // Set up map UI settings
        googleMap?.uiSettings?.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = true
        }
        
        // Observe restaurants data
        viewModel.glutenFreeRestaurants.observe(viewLifecycleOwner) { restaurants ->
            displayRestaurantsOnMap(restaurants)
        }
        
        // Get current location and center map
        getCurrentLocationAndCenterMap()
    }
    
    /**
     * Get current location and center the map on it
     */
    private fun getCurrentLocationAndCenterMap() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    googleMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
                    )
                }
            }
        }
    }
    
    /**
     * Get current location and search for gluten-free restaurants
     */
    private fun getCurrentLocationAndSearch() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.progressBar.visibility = View.VISIBLE
            
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.searchNearbyGlutenFreeRestaurants(
                        it.latitude,
                        it.longitude
                    )
                }
                
                binding.progressBar.visibility = View.GONE
            }
        }
    }
    
    /**
     * Display restaurants on the map
     */
    private fun displayRestaurantsOnMap(restaurants: List<Restaurant>) {
        googleMap?.clear()
        
        restaurants.forEach { restaurant ->
            val position = LatLng(restaurant.latitude, restaurant.longitude)
            val markerOptions = MarkerOptions()
                .position(position)
                .title(restaurant.name)
                .snippet("Rating: ${restaurant.rating}")
            
            // Use different marker color for gluten-free restaurants
            if (restaurant.isGlutenFree) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            } else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
            
            googleMap?.addMarker(markerOptions)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}