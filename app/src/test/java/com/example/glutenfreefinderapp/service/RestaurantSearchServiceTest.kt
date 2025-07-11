package com.example.glutenfreefinderapp.service

import android.content.Context
import android.content.res.Resources
import com.example.glutenfreefinderapp.model.Restaurant
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Review
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
class RestaurantSearchServiceTest {
    
    @Mock
    private lateinit var mockContext: Context
    
    @Mock
    private lateinit var mockResources: Resources
    
    @Mock
    private lateinit var mockPlacesClient: PlacesClient
    
    @Mock
    private lateinit var mockLocationService: LocationService
    
    private lateinit var restaurantSearchService: RestaurantSearchService
    
    private val userLocation = LatLng(37.7749, -122.4194) // San Francisco
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        
        // Mock resources for API key
        `when`(mockContext.resources).thenReturn(mockResources)
        `when`(mockResources.getIdentifier(any(), any(), any())).thenReturn(123)
        `when`(mockContext.getString(123)).thenReturn("test_api_key")
        
        // Set up Places API mock
        Places.initialize(mockContext, "test_api_key")
        
        // Create test instance with mocked dependencies
        restaurantSearchService = RestaurantSearchService(mockContext)
        
        // Use reflection to set the mocked placesClient
        val placesClientField = RestaurantSearchService::class.java.getDeclaredField("placesClient")
        placesClientField.isAccessible = true
        placesClientField.set(restaurantSearchService, mockPlacesClient)
        
        // Use reflection to set the mocked locationService
        val locationServiceField = RestaurantSearchService::class.java.getDeclaredField("locationService")
        locationServiceField.isAccessible = true
        locationServiceField.set(restaurantSearchService, mockLocationService)
    }
    
    @Test
    fun `searchNearbyRestaurants returns only restaurants that are gluten free friendly`() = runTest {
        // Arrange
        // Mock the findAutocompletePredictions call
        val mockPredictionsResponse = mock(FindAutocompletePredictionsResponse::class.java)
        val mockPrediction1 = mock(AutocompletePrediction::class.java)
        val mockPrediction2 = mock(AutocompletePrediction::class.java)
        
        `when`(mockPrediction1.placeId).thenReturn("place1")
        `when`(mockPrediction2.placeId).thenReturn("place2")
        `when`(mockPredictionsResponse.autocompletePredictions).thenReturn(listOf(mockPrediction1, mockPrediction2))
        
        val predictionsTask: Task<FindAutocompletePredictionsResponse> = Tasks.forResult(mockPredictionsResponse)
        `when`(mockPlacesClient.findAutocompletePredictions(any())).thenReturn(predictionsTask)
        
        // Mock the fetchPlace calls
        // Restaurant 1: Has both gluten-free and celiac mentions
        val mockPlace1 = mock(Place::class.java)
        `when`(mockPlace1.id).thenReturn("place1")
        `when`(mockPlace1.name).thenReturn("Gluten Free Restaurant")
        `when`(mockPlace1.address).thenReturn("123 Main St")
        `when`(mockPlace1.latLng).thenReturn(LatLng(37.78, -122.41))
        `when`(mockPlace1.rating).thenReturn(4.5)
        
        val mockReview1 = mock(Review::class.java)
        `when`(mockReview1.text).thenReturn("Great gluten free options!")
        val mockReview2 = mock(Review::class.java)
        `when`(mockReview2.text).thenReturn("Perfect for celiac disease.")
        `when`(mockPlace1.reviews).thenReturn(listOf(mockReview1, mockReview2))
        
        val fetchResponse1 = mock(FetchPlaceResponse::class.java)
        `when`(fetchResponse1.place).thenReturn(mockPlace1)
        val fetchTask1: Task<FetchPlaceResponse> = Tasks.forResult(fetchResponse1)
        
        // Restaurant 2: Only has gluten-free mentions, no celiac
        val mockPlace2 = mock(Place::class.java)
        `when`(mockPlace2.id).thenReturn("place2")
        `when`(mockPlace2.name).thenReturn("Regular Restaurant")
        `when`(mockPlace2.address).thenReturn("456 Oak St")
        `when`(mockPlace2.latLng).thenReturn(LatLng(37.77, -122.42))
        `when`(mockPlace2.rating).thenReturn(4.0)
        
        val mockReview3 = mock(Review::class.java)
        `when`(mockReview3.text).thenReturn("They have some gluten free options.")
        val mockReview4 = mock(Review::class.java)
        `when`(mockReview4.text).thenReturn("Good service.")
        `when`(mockPlace2.reviews).thenReturn(listOf(mockReview3, mockReview4))
        
        val fetchResponse2 = mock(FetchPlaceResponse::class.java)
        `when`(fetchResponse2.place).thenReturn(mockPlace2)
        val fetchTask2: Task<FetchPlaceResponse> = Tasks.forResult(fetchResponse2)
        
        // Set up the fetchPlace method to return different responses based on placeId
        `when`(mockPlacesClient.fetchPlace(any())).thenAnswer { invocation ->
            val request = invocation.arguments[0] as FetchPlaceRequest
            val placeId = request.placeId
            if (placeId == "place1") fetchTask1 else fetchTask2
        }
        
        // Mock distance calculation
        `when`(mockLocationService.calculateDistance(any(), any())).thenReturn(1000f)
        
        // Act
        val results = restaurantSearchService.searchNearbyRestaurants(userLocation)
        
        // Assert
        assertEquals(1, results.size)
        assertEquals("Gluten Free Restaurant", results[0].name)
        assertTrue(results[0].hasGlutenFreeReviews)
        assertTrue(results[0].hasCeliacReviews)
    }
    
    @Test
    fun `searchNearbyRestaurants uses correct bounds for search area`() = runTest {
        // Arrange
        val mockPredictionsResponse = mock(FindAutocompletePredictionsResponse::class.java)
        `when`(mockPredictionsResponse.autocompletePredictions).thenReturn(emptyList())
        
        val predictionsTask: Task<FindAutocompletePredictionsResponse> = Tasks.forResult(mockPredictionsResponse)
        `when`(mockPlacesClient.findAutocompletePredictions(any())).thenReturn(predictionsTask)
        
        // Capture the request to verify bounds
        val requestCaptor = ArgumentCaptor.forClass(FindAutocompletePredictionsRequest::class.java)
        
        // Act
        restaurantSearchService.searchNearbyRestaurants(userLocation, 1000.0)
        
        // Assert
        verify(mockPlacesClient).findAutocompletePredictions(requestCaptor.capture())
        val capturedRequest = requestCaptor.value
        
        // Verify that locationBias is a RectangularBounds
        assertTrue(capturedRequest.locationBias is RectangularBounds)
    }
}