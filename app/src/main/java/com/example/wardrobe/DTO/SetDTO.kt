package com.example.wardrobe.DTO

data class SetDTO(
    var userID: String? = null,
    var imageRef: String? = null,
    var topRef: String? = null,
    var bottomRef: String? = null,
    var season: String? = null,
    var memo: String? = null,
    var isPublic: Boolean = true,
    var likedUser: List<String>? = arrayListOf(),
    var hashtag: List<String>? = arrayListOf()
)
