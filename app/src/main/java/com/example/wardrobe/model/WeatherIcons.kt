package com.example.wardrobe.model

import androidx.annotation.DrawableRes
import com.example.wardrobe.R

object WeatherIcons {
    val sunny = R.drawable.wi_day_sunny
    val thunder = R.drawable.wi_storm_showers
    val cloudy = R.drawable.wi_cloudy
    val rainy = R.drawable.wi_rain
    val snowy = R.drawable.wi_snow
    val windy = R.drawable.wi_windy
    val dust = R.drawable.wi_dust
    val tornado = R.drawable.wi_tornado
    val notApplicable = R.drawable.wi_na
}

data class DrawableIcon(@DrawableRes val id: Int)
