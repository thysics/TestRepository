package com.example.glutenfreefinderapp.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.glutenfreefinderapp.GlutenFreeFinderApp
import com.example.glutenfreefinderapp.databinding.FragmentRestaurantListBinding
import com.example.glutenfreefinderapp.model.Restaurant
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class RestaurantListFragment : Fragment() {

    private var _binding: FragmentRestaurantListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: RestaurantListViewModel
    private lateinit var adapter: RestaurantAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantListBinding.inflate(inflater, container, false)
        
        // Initialize ViewModel
        val repository = (requireActivity().application as GlutenFreeFinderApp).repository
        val viewModelFactory = RestaurantListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[RestaurantListViewModel::class.java]
        
        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        
        // Set up RecyclerView
        adapter = RestaurantAdapter { restaurant ->
            // Handle restaurant click
            // Navigate to detail screen
        }
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@RestaurantListFragment.adapter
        }
        
        // Set up search button
        binding.btnSearch.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            getCurrentLocationAndSearch()
        }
        
        // Observe restaurants data
        viewModel.glutenFreeRestaurants.observe(viewLifecycleOwner) { restaurants ->
            binding.progressBar.visibility = View.GONE
            if (restaurants.isEmpty()) {
                binding.tvNoResults.visibility = View.VISIBLE
            } else {
                binding.tvNoResults.visibility = View.GONE
                updateRestaurantList(restaurants)
            }
        }
        
        return binding.root
    }
    
    /**
     * Get current location and search for gluten-free restaurants
     */
    private fun getCurrentLocationAndSearch() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                viewModel.searchNearbyGlutenFreeRestaurants(
                    it.latitude,
                    it.longitude
                )
            }
        }
    }
    
    /**
     * Update the restaurant list with current location for distance calculation
     */
    private fun updateRestaurantList(restaurants: List<Restaurant>) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let { currentLocation ->
                // Sort restaurants by distance
                val sortedRestaurants = restaurants.sortedBy { restaurant ->
                    restaurant.distanceFrom(currentLocation.latitude, currentLocation.longitude)
                }
                adapter.submitList(sortedRestaurants)
            } ?: adapter.submitList(restaurants)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}