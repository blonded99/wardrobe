package com.example.wardrobe.viewmodel

import android.annotation.SuppressLint
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wardrobe.model.WeatherNow
import com.example.wardrobe.network.WeatherApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

data class HomeItem(val clothesImageUrl: String)

@HiltViewModel
class HomeViewModel @Inject constructor(
    val fusedLocationClient: FusedLocationProviderClient,
    val weatherApi: WeatherApi,
    val dataStore: DataStore<Preferences>,
) : ViewModel() {
    val HomeweatherItems = ArrayList<HomeItem>()
    val HomeweatherItemsListData = MutableLiveData<ArrayList<HomeItem>>()

    val HomecommunityItems = ArrayList<HomeItem>()
    val HomecommunityItemsListData = MutableLiveData<ArrayList<HomeItem>>()

    val weatherData = MutableLiveData<WeatherNow>()

    private val LAT_KEY = doublePreferencesKey("lat_key")
    private val LON_KEY = doublePreferencesKey("lon_key")
    private val LAST_UPDATED = longPreferencesKey("last_updated")

    private fun isUpdateNeeded(): Flow<Boolean> = dataStore.data.map { data ->
        val now = Clock.System.now()
        val epoch = data[LAST_UPDATED] ?: 0
        val duration = now - Instant.fromEpochSeconds(epoch)
        duration.inWholeMinutes > 15
    }

    private fun cachedLatLon() = dataStore.data.map { data ->
        val lat = data[LAT_KEY]
        val lon = data[LON_KEY]
        if (lat == null || lon == null) {
            null
        } else {
            lat to lon
        }
    }

    fun fetchWeatherForecast() {
        viewModelScope.launch {
            val cache = cachedLatLon().first()
            val latLon = if (cache == null || isUpdateNeeded().first()) {
                @SuppressLint("MissingPermission")
                val freshLocation = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    null
                ).await()

                dataStore.edit { data ->
                    data[LAT_KEY] = freshLocation.latitude
                    data[LON_KEY] = freshLocation.longitude
                }
                freshLocation.latitude to freshLocation.longitude

            } else {
                cache
            }

            val freshWeatherNow = weatherApi.getWeatherReport(latLon.first, latLon.second)
            weatherData.value = freshWeatherNow
            dataStore.edit { data ->
                data[LAST_UPDATED] = freshWeatherNow.timeUTC.epochSeconds
            }
        }
    }

    /* 옷장 이미지 */

    fun addHomeWeatherItem(item: HomeItem) {
        HomeweatherItems.add(item)
        HomeweatherItemsListData.value = HomeweatherItems
    }
    /* 커뮤니티 이미지 */

    fun addHomeCommunityItem(item: HomeItem) {
        HomecommunityItems.add(item)
        HomecommunityItemsListData.value = HomecommunityItems
    }

    fun deleteHomeWeatherItem() {
        HomeweatherItems.clear()
        HomeweatherItemsListData.value?.clear()
    }

    fun deleteHomeCommunityItem() {
        HomecommunityItems.clear()
        HomecommunityItemsListData.value?.clear()

    }

}
