# Gluten Free Finder App

An Android application that helps users find gluten-free and celiac-friendly restaurants nearby.

## Features

- Search for restaurants that mention both "gluten free" and "celiac" in their Google Maps reviews
- View restaurants on a map with detailed information
- Filter restaurants based on distance and ratings
- See excerpts from reviews mentioning gluten-free options

## Setup Instructions

1. Clone this repository
2. Open the project in Android Studio
3. Get a Google Maps API key from the [Google Cloud Console](https://console.cloud.google.com/)
4. Add your API key to `app/src/main/res/values/google_maps_api.xml`
5. Build and run the application

## API Key Configuration

You need to add your Google Maps API key in two places:

1. In `app/src/main/res/values/google_maps_api.xml`
2. In the `AndroidManifest.xml` file, replace `YOUR_API_KEY` with your actual API key

Make sure your API key has the following APIs enabled:
- Maps SDK for Android
- Places API

## Architecture

The application follows a clean architecture approach with the following components:

- **Model**: Data classes representing restaurants and their attributes
- **Service**: Classes for interacting with Google Maps/Places APIs and location services
- **Adapter**: RecyclerView adapter for displaying restaurant lists
- **Activity**: Main UI components and user interaction

## Testing

Unit tests are provided for the core functionality:
- Restaurant model tests
- Restaurant search service tests

Run tests using:
```
./gradlew test
```