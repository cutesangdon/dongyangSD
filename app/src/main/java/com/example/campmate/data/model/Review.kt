package com.example.campmate.data.model

import com.google.gson.annotations.SerializedName

//11.10 수정_KM 리뷰
data class Review(
    // DB: id
    @SerializedName("id")
    val reviewId: Long,
    @SerializedName("reservation_id")
    val reservationId: Long,
    // DB: customer_id (필요 시)
    @SerializedName("customer_id")
    val customerId: Long,
    // DB: camping_zone_id
    @SerializedName("camping_zone_id")
    val campingZoneId: Int,
    // DB에 없는 필드이지만, 목록 표시를 위해 백엔드에서 조인하여 받아와야 함, 확인 필요
    @SerializedName("campsiteName")
    val campsiteName: String = "알 수 없음",

    // DB: rating
    @SerializedName("rating")
    val rating: Float,
    // DB: coment -> 프론트엔드에서는 content로 통일
    @SerializedName("coment")
    val content: String,
    // DB: create_dt
    @SerializedName("create_dt")
    val createdAt: String,
    @SerializedName("campsite_name")
    val campsite: String = "캠핑장 정보 없음",
    @SerializedName("author_name")
    val authorName: String = "익명"

)