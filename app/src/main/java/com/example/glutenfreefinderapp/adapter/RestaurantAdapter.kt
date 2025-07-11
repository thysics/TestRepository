package com.example.glutenfreefinderapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.glutenfreefinderapp.R
import com.example.glutenfreefinderapp.model.Restaurant
import java.text.DecimalFormat

/**
 * Adapter for displaying restaurants in a RecyclerView
 */
class RestaurantAdapter(private val onItemClick: (Restaurant) -> Unit) : 
    ListAdapter<Restaurant, RestaurantAdapter.RestaurantViewHolder>(RestaurantDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view, onItemClick)
    }
    
    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class RestaurantViewHolder(
        itemView: View,
        private val onItemClick: (Restaurant) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val nameTextView: TextView = itemView.findViewById(R.id.restaurantNameTextView)
        private val addressTextView: TextView = itemView.findViewById(R.id.restaurantAddressTextView)
        private val distanceTextView: TextView = itemView.findViewById(R.id.restaurantDistanceTextView)
        private val ratingTextView: TextView = itemView.findViewById(R.id.restaurantRatingTextView)
        private val glutenFreeTagTextView: TextView = itemView.findViewById(R.id.glutenFreeTagTextView)
        private val celiacFriendlyTagTextView: TextView = itemView.findViewById(R.id.celiacFriendlyTagTextView)
        
        private val distanceFormat = DecimalFormat("#.# miles")
        
        fun bind(restaurant: Restaurant) {
            nameTextView.text = restaurant.name
            addressTextView.text = restaurant.address
            
            // Format distance
            val distanceInMiles = restaurant.getDistanceInMiles()
            distanceTextView.text = distanceFormat.format(distanceInMiles)
            
            // Set rating
            ratingTextView.text = "Rating: ${restaurant.rating}"
            
            // Set tag visibility
            glutenFreeTagTextView.visibility = if (restaurant.hasGlutenFreeReviews) View.VISIBLE else View.GONE
            celiacFriendlyTagTextView.visibility = if (restaurant.hasCeliacReviews) View.VISIBLE else View.GONE
            
            // Set click listener
            itemView.setOnClickListener { onItemClick(restaurant) }
        }
    }
    
    /**
     * DiffUtil callback for efficient RecyclerView updates
     */
    class RestaurantDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem == newItem
        }
    }
}