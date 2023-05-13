package com.example.wardrobe.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Item(val clothesImageUrl: Int)

class WordrobeViewModel: ViewModel() {
    val items = ArrayList<Item>()
    val itemsListData = MutableLiveData<ArrayList<Item>>()


    /* 옷장 이미지 */

    fun addItem(item: Item){
        items.add(item)
        itemsListData.value = items
    }

    fun deleteItem(pos: Int){
        items.removeAt(pos)
        itemsListData.value = items
    }

}