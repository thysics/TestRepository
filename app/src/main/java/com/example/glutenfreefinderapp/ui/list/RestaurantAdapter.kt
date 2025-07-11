package com.example.glutenfreefinderapp.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.glutenfreefinderapp.databinding.ItemRestaurantBinding
import com.example.glutenfreefinderapp.model.Restaurant
import kotlin.math.roundToInt

/**
 * Adapter for displaying restaurants in a RecyclerView
 */
class RestaurantAdapter(
    private val onItemClick: (Restaurant) -> Unit
) : ListAdapter<Restaurant, RestaurantAdapter.RestaurantViewHolder>(RestaurantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class RestaurantViewHolder(
        private val binding: ItemRestaurantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(restaurant: Restaurant, onItemClick: (Restaurant) -> Unit) {
            binding.apply {
                tvName.text = restaurant.name
                tvAddress.text = restaurant.address
                
                // Format rating with review count
                val ratingText = if (restaurant.rating > 0) {
                    "${restaurant.rating} (${restaurant.totalReviews})"
                } else {
                    "No ratings"
                }
                tvRating.text = ratingText
                
                // Show gluten-free badge
                if (restaurant.isGlutenFree) {
                    tvGlutenFreeBadge.visibility = android.view.View.VISIBLE
                    val reviewText = "GF: ${restaurant.glutenFreeReviewCount} | Celiac: ${restaurant.celiacReviewCount}"
                    tvGlutenFreeReviews.text = reviewText
                    tvGlutenFreeReviews.visibility = android.view.View.VISIBLE
                } else {
                    tvGlutenFreeBadge.visibility = android.view.View.GONE
                    tvGlutenFreeReviews.visibility = android.view.View.GONE
                }
                
                // Set click listener
                root.setOnClickListener {
                    onItemClick(restaurant)
                }
            }
        }
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