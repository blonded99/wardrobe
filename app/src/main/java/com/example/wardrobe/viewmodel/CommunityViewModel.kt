package com.example.wardrobe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class CommunityItem(val clothesImageUrl: String)

class CommunityViewModel: ViewModel() {

    val communityMainItems = ArrayList<CommunityItem>()
    val communityMainItemsListData = MutableLiveData<ArrayList<CommunityItem>>()

    val communityLikedItems = ArrayList<CommunityItem>()
    val communityLikedItemsListData = MutableLiveData<ArrayList<CommunityItem>>()


    /* 커뮤니티 이미지 */

    fun addCommunityItem(item: CommunityItem, which: String){
        if(which == "main"){
            communityMainItems.add(item)
            communityMainItemsListData.value = communityMainItems
        }
        else if(which == "liked"){
            communityLikedItems.add(item)
            communityLikedItemsListData.value = communityLikedItems
        }
    }


    fun deleteCommunityItem(which: String){
        if(which == "main"){
            communityMainItems.clear()
            communityMainItemsListData.value?.clear()
        }
        else if(which == "liked"){
            communityLikedItems.clear()
            communityLikedItemsListData.value?.clear()
        }
    }

}