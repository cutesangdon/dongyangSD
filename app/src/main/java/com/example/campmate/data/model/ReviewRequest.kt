package com.example.campmate.data.model

import com.google.gson.annotations.SerializedName
import java.time.ZoneId

data class ReviewRequest(
    //11.10 수정 KM 리뷰
    //11.13 수정 DH
    @SerializedName("camping_zone_id") val campingZoneId: Int,
    @SerializedName("reservation_id") val reservationId: Long,
    @SerializedName("customers_Id") val customerId: Long,
    @SerializedName("rating") val rating: Float,
    // coment -> content로 통일
    @SerializedName("coment") val content: String
)