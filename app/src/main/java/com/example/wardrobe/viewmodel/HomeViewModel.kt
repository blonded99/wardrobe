package com.example.wardrobe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class HomeItem(val clothesImageUrl: String)

class HomeViewModel: ViewModel() {
    val HomeweatherItems = ArrayList<HomeItem>()
    val HomeweatherItemsListData = MutableLiveData<ArrayList<HomeItem>>()

    val HomecommunityItems = ArrayList<HomeItem>()
    val HomecommunityItemsListData = MutableLiveData<ArrayList<HomeItem>>()

    /* 옷장 이미지 */

    fun addHomeWeatherItem(item: HomeItem){
        HomeweatherItems.add(item)
        HomeweatherItemsListData.value = HomeweatherItems
    }
    /* 커뮤니티 이미지 */

    fun addHomeCommunityItem(item: HomeItem){
        HomecommunityItems.add(item)
        HomecommunityItemsListData.value = HomecommunityItems
    }

    fun deleteHomeWeatherItem(){
        HomeweatherItems.clear()
        HomecommunityItemsListData.value?.clear()
    }

    fun deleteHomeCommunityItem(){
        HomecommunityItems.clear()
        HomecommunityItemsListData.value?.clear()

    }

}