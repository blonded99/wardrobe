package com.example.wardrobe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class HomeItem(val clothesImageUrl: String)
data class TempHomeItem(val clothesImageUrl: Int)

class HomeViewModel: ViewModel() {
    val HomeweatherItems = ArrayList<TempHomeItem>()
    val HomeweatherItemsListData = MutableLiveData<ArrayList<TempHomeItem>>()

    val HomecommunityItems = ArrayList<HomeItem>()
    val HomecommunityItemsListData = MutableLiveData<ArrayList<HomeItem>>()

    /* 옷장 이미지 */

    fun addHomeWeatherItem(item: TempHomeItem){
        HomeweatherItems.add(item)
        HomeweatherItemsListData.value = HomeweatherItems
    }
    /* 커뮤니티 이미지 */

    fun addHomeCommunityItem(item: HomeItem){
        HomecommunityItems.add(item)
        HomecommunityItemsListData.value = HomecommunityItems
    }


    fun deleteHomeCommunityItem(){
        HomecommunityItems.clear()
        HomecommunityItemsListData.value?.clear()

    }

}