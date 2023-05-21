package com.example.wardrobe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class CodiItem(val clothesImageUrl: String)

class CodiViewModel: ViewModel() {
    val CodiAllItems = ArrayList<CodiItem>()
    val CodiAllItemsListData = MutableLiveData<ArrayList<CodiItem>>()

    val CodiPublicItems = ArrayList<CodiItem>()
    val CodiPublicItemsListData = MutableLiveData<ArrayList<CodiItem>>()

    val CodiPrivateItems = ArrayList<CodiItem>()
    val CodiPrivateItemsListData = MutableLiveData<ArrayList<CodiItem>>()


    /* 코디 탭 이미지 */

    fun addCodiItem(item: CodiItem,which: String){
        if(which.equals("all")){
            CodiAllItems.add(item)
            CodiAllItemsListData.value = CodiAllItems
        }
        else if(which.equals("public")){
            CodiPublicItems.add(item)
            CodiPublicItemsListData.value = CodiPublicItems
        }
        else if(which.equals("private")){
            CodiPrivateItems.add(item)
            CodiPrivateItemsListData.value = CodiPrivateItems
        }
    }


    fun deleteCodiItem(which: String){
        if(which.equals("all")){
            CodiAllItems.clear()
            CodiAllItemsListData.value?.clear()
        }
        else if(which.equals("public")){
            CodiPublicItems.clear()
            CodiPublicItemsListData.value?.clear()
        }
        else if(which.equals("private")){
            CodiPrivateItems.clear()
            CodiPrivateItemsListData.value?.clear()
        }

    }


}