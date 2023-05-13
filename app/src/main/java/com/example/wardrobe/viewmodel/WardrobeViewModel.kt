package com.example.wardrobe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Item(val clothesImageUrl: Int)

class WordrobeViewModel: ViewModel() {
    val wardrobeItems = ArrayList<Item>()
    val wardrobeItemsListData = MutableLiveData<ArrayList<Item>>()

    val communityItems = ArrayList<Item>()
    val communityItemsListData = MutableLiveData<ArrayList<Item>>()

    /* 옷장 이미지 */

    fun addWardrobeItem(item: Item){
        wardrobeItems.add(item)
        wardrobeItemsListData.value = wardrobeItems
    }

    fun deleteWardrobeItem(pos: Int){
        wardrobeItems.removeAt(pos)
        wardrobeItemsListData.value = wardrobeItems
    }


    /* 커뮤니티 이미지 */

    fun addCommunityItem(item: Item){
        communityItems.add(item)
        communityItemsListData.value = wardrobeItems
    }

}