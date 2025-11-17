// CampsiteSite.kt

package com.example.campmate.data.model

import com.google.gson.annotations.SerializedName

// 캠핑장 내의 개별 사이트(존) 정보를 담는 클래스
data class CampsiteSite(
    // (수정) 타입을 String에서 Long으로 변경해야
    // ReservationRequest(campingZoneId = Long)와 타입이 일치합니다.
    @SerializedName("siteId")
    val siteId: Long,

    @SerializedName("name")
    val name: String?,

    @SerializedName("price")
    val price: Int?
)