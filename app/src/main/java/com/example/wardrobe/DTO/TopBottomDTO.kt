package com.example.wardrobe.DTO

data class TopBottomDTO(
    var userID: String? = null,
    var imageRef: String? = null,
    var thickness: String? = null,
    var season: String? = null,
    var length: String? = null,
    var brand: String? = null,
    var size: String? = null,
    var memo: String? = null,
    var hashtag: List<String>? = arrayListOf()
)
