# Gluten Free Finder App

An Android application that helps users find gluten-free restaurants near their location.

## Features

- **Search for Gluten-Free Restaurants**: The app searches for restaurants that mention both "gluten free" and "celiac" in their Google Maps reviews.
- **Map View**: Displays gluten-free restaurants on a map with color-coded markers.
- **List View**: Shows a list of gluten-free restaurants sorted by distance from the user.
- **Restaurant Details**: Provides information about each restaurant including address, rating, and review highlights.

## Technical Details

### Architecture

The app follows the MVVM (Model-View-ViewModel) architecture pattern:
- **Model**: Restaurant and Review data classes
- **View**: Activities and Fragments for UI
- **ViewModel**: Manages UI-related data and business logic

### Key Components

- **Google Maps Integration**: Uses Google Maps API to search for restaurants and display them on a map
- **Room Database**: Local storage for restaurant data
- **Retrofit**: Network requests to Google Maps API
- **LiveData & Flow**: Reactive data handling
- **Coroutines**: Asynchronous operations

## Setup Instructions

1. Clone the repository
2. Add your Google Maps API key in the AndroidManifest.xml file
3. Build and run the app

## Testing

The app includes comprehensive unit tests for the Restaurant and Review models, ensuring that the gluten-free detection logic works correctly.