// Campsite.kt

package com.example.campmate.data.model

data class Campsite(
    val id: Int,
    val name: String,
    val address: String,
    val imageUrl: String,
    val rating: Float,
    //  이 캠핑장이 보유한 사이트(존) 목록
    val sites: List<CampsiteSite> = emptyList() // 상세 정보 화면에서만 사용될 수 있음
)