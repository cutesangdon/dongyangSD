package com.example.campmate.data.model

data class Review(
    val reviewId: Int,
    val campsiteId: Int, // ✅✅✅ [추가] 빠져있던 campsiteId 필드입니다. ✅✅✅
    val campsiteName: String,
    val authorName: String,
    val rating: Float,
    val content: String,
    val imageUrls: List<String>,
    val createdAt: String
)