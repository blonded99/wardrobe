package com.example.wardrobe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Homeitem(val clothesImageUrl: Int)

class HomeViewModel: ViewModel() {
    val HomeweatherItems = ArrayList<Homeitem>()
    val HomeweatherItemsListData = MutableLiveData<ArrayList<Homeitem>>()

    val HomecommunityItems = ArrayList<Homeitem>()
    val HomecommunityItemsListData = MutableLiveData<ArrayList<Homeitem>>()

    /* 옷장 이미지 */

    fun addHomeWeatherItem(item: Homeitem){
        HomeweatherItems.add(item)
        HomeweatherItemsListData.value = HomeweatherItems
    }
    /* 커뮤니티 이미지 */

    fun addHomeCommunityItem(item: Homeitem){
        HomecommunityItems.add(item)
        HomecommunityItemsListData.value = HomecommunityItems
    }

}