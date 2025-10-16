// CampsiteSite.kt

package com.example.campmate.data.model

// 캠핑장 내의 개별 사이트(존) 정보를 담는 클래스
data class CampsiteSite(
    val siteId: String, // 예: "A-1", "B-2", "글램핑-1"
    val siteName: String,
    val pricePerNight: Int // 1박당 가격
)