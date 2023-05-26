package com.example.wardrobe.network

import com.example.wardrobe.model.Weather
import com.example.wardrobe.model.WeatherNow
import com.example.wardrobe.network.model.WeatherNowResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.URL

private interface OpenWeatherMapApi {
    @GET(value = "/weather")
    suspend fun getWeather(
        @Query("lat") lat:  Double,
        @Query("lon") lon:  Double,
        @Query("appid") apiKey:  String,
        @Query("units") unit:  String?,
        @Query("lang") lang:  String?,
    ): WeatherNowResponse
}


class WeatherApi(val apiKey: String): WeatherDataSource {

    private val json = Json {
        ignoreUnknownKeys
    }

    private val client = Retrofit.Builder()
        .baseUrl(HttpUrl.Builder()
            .scheme("https")
            .host("api.openweathermap.org")
            .addPathSegments("/data/2.5")
            .build())
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType())
        ).build()
        .create(OpenWeatherMapApi::class.java)

    override suspend fun getWeatherReport(latitude: Double, longitude: Double): WeatherNow {
        return client.getWeather(
            lat = latitude,
            lon = longitude,
            apiKey = apiKey,
            unit = "metric",
            lang = "kr",
        ).let {
            WeatherNow(
                location = it.name,
                description = it.weathers.getOrNull(0)?.description ?: "-",
                type = it.weathers.getOrNull(0)?.toWeather() ?: Weather.NA,
                temperature = it.main.temp,
                feelsLike = it.main.feels_like,

            )
        }
    }

}
