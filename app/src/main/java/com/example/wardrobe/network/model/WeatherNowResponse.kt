package com.example.wardrobe.network.model

import com.example.wardrobe.model.Weather
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class WeatherNowResponse(
    val coord: Coordinate,
    val weathers: List<WeatherData>,
    @Transient val base: String = "", // Internal parameter
    val main: MainForecast,
    val visibility: UInt,
    val wind: WindData,
    @Transient val clouds: Map<String, Int>? = null,
    val rain: PrecipitationData?,
    val snow: PrecipitationData?,
    val dt: UInt,
    val sys: SunData,
    val timezone: Int, // Shift in seconds from UTC
    val id: Int,
    val name: String,
    @Transient val cod: Int = 200, // Internal parameter
)


@Serializable
data class Coordinate(
    val lon: Double,
    val lat: Double,
)

@Serializable
data class WeatherData(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
) {
    fun toWeather(): Weather = when(id) {
        in 200 until 300 -> Weather.THUNDER_STORM
        in 300 until 400 -> Weather.DRIZZLE
        in 500..504,in 520..522, 531  -> Weather.RAINY
        in 600 until 700, 511 -> Weather.SNOWY
        701 -> Weather.MIST
        711 -> Weather.SMOKE
        721 -> Weather.HAZE
        731,761->Weather.DUST
        741->Weather.FOG
        751->Weather.SAND
        762->Weather.ASH
        771-> Weather.SQUALL
        781->Weather.TORNADO
        800 -> Weather.CLEAR
        in 801 until 900 -> Weather.CLOUDY
        else -> Weather.NA
    }
}

@Serializable
data class MainForecast(
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val humidity: UInt,
    val pressure: Int,
    val sea_level: Int?,
    val grnd_level: Int?,
)

@Serializable
data class WindData(
    val speed: Float,
    val deg: Int,
    val gust: Float?,
)

@Serializable
data class PrecipitationData(
    val `1h`: UInt?,
    val `3h`: UInt?,
)

@Serializable
data class SunData(
    @Transient val type: Int = -1, // Internal parameter
    @Transient val id: Int = -1, // Internal parameter
    @Transient val message: String? = null, // Internal parameter
    val country: String, // Country code (GB, JP etc.)
    val sunrise: UInt, // Sunrise time, unix, UTC
    val sunset: UInt, // Sunset time, unix, UTC
)

