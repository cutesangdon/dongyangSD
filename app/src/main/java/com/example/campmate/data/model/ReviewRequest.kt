package com.example.campmate.data.model

import com.google.gson.annotations.SerializedName

data class ReviewRequest(
    @SerializedName("campsiteId") val campsiteId: Int,
    @SerializedName("rating") val rating: Float,
    @SerializedName("content") val content: String,
    // TODO: 실제로는 로그인된 사용자 ID와 예약 ID를 보내야 합니다.
    @SerializedName("customerId") val customerId: Long = 1L, // 임시
    @SerializedName("reservationId") val reservationId: Long = 1L // 임시
)