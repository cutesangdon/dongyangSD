package com.example.campmate.data.model

import com.google.gson.annotations.SerializedName

/**
 * DB reservations 테이블에 데이터를 저장하기 위해
 * 안드로이드 앱이 백엔드로 보내는 요청(Request) 데이터 모델
 */
data class ReservationRequest(
    @SerializedName("campingZoneId") // 백엔드의 ReservationRequestDTO.java 필드명과 일치
    val campingZoneId: Long,

    @SerializedName("adminsId")
    val adminsId: Long,

    @SerializedName("checkIn")
    val checkIn: String, // "yyyy-MM-dd" 형식

    @SerializedName("checkOut")
    val checkOut: String, // "yyyy-MM-dd" 형식

    @SerializedName("adults")
    val adults: Int,

    @SerializedName("children")
    val children: Int
)