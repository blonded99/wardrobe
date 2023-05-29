package com.example.wardrobe.network

import com.example.wardrobe.BuildConfig
import com.example.wardrobe.model.Weather
import com.example.wardrobe.model.WeatherNow
import com.example.wardrobe.network.model.WeatherNowResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

private interface OpenWeatherMapApi {
    @GET(value = "/data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") unit: String?,
        @Query("lang") lang: String?,
    ): WeatherNowResponse
}

@Singleton
class WeatherApi : WeatherDataSource {

    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) setLevel(HttpLoggingInterceptor.Level.BODY)
        }).build()

    private val client = Retrofit.Builder()
        .baseUrl(
            HttpUrl.Builder()
                .scheme("https")
                .host("api.openweathermap.org")
                .build()
        )
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType())
        ).client(okHttpClient)
        .build()
        .create(OpenWeatherMapApi::class.java)

    override suspend fun getWeatherReport(latitude: Double, longitude: Double): WeatherNow {
        return client.getWeather(
            lat = latitude,
            lon = longitude,
            apiKey = "f06661fbab30a4fd24d81533bc584e12",
            unit = "metric",
            lang = "kr",
        ).let {
            WeatherNow(
                location = it.name,
                description = it.weather.getOrNull(0)?.description ?: "-",
                type = it.weather.getOrNull(0)?.toWeather() ?: Weather.NA,
                temperature = it.main.temp,
                feelsLike = it.main.feels_like,
                timeUTC = Instant.fromEpochSeconds(it.dt.toLong())
            )
        }
    }

}
