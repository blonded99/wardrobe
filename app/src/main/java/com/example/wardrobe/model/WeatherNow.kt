package com.example.wardrobe.model

data class WeatherNow(
    val type: Weather,
    val description: String,
    val location: String,
    val temperature: Float,
    val feelsLike: Float,
)

enum class Weather(val icon: DrawableIcon) {
    CLEAR(DrawableIcon(WeatherIcons.sunny)),
    THUNDER_STORM(DrawableIcon(WeatherIcons.thunder)),
    CLOUDY(DrawableIcon(WeatherIcons.cloudy)),
    RAINY(DrawableIcon(WeatherIcons.rainy)),
    DRIZZLE(DrawableIcon(WeatherIcons.rainy)),
    SNOWY(DrawableIcon(WeatherIcons.snowy)),
    MIST(DrawableIcon(WeatherIcons.windy)),
    SMOKE(DrawableIcon(WeatherIcons.dust)),
    HAZE(DrawableIcon(WeatherIcons.windy)),
    DUST(DrawableIcon(WeatherIcons.dust)),
    FOG(DrawableIcon(WeatherIcons.dust)),
    SAND(DrawableIcon(WeatherIcons.dust)),
    ASH(DrawableIcon(WeatherIcons.dust)),
    SQUALL(DrawableIcon(WeatherIcons.tornado)),
    TORNADO(DrawableIcon(WeatherIcons.tornado)),
    NA(DrawableIcon(WeatherIcons.notApplicable))
}
