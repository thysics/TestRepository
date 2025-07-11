package com.example.glutenfreefinderapp.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.glutenfreefinderapp.model.Restaurant

/**
 * Room database for storing restaurant data
 */
@Database(entities = [Restaurant::class], version = 1, exportSchema = false)
abstract class RestaurantDatabase : RoomDatabase() {
    
    abstract fun restaurantDao(): RestaurantDao
    
    companion object {
        @Volatile
        private var INSTANCE: RestaurantDatabase? = null
        
        fun getDatabase(context: Context): RestaurantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RestaurantDatabase::class.java,
                    "restaurant_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}