package com.example.wardrobe.network

import com.example.wardrobe.model.WeatherNow

interface WeatherDataSource {
    suspend fun getWeatherReport(latitude: Double, longitude: Double): WeatherNow
}
