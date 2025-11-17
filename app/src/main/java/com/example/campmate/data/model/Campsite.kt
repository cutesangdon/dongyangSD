// Campsite.kt

package com.example.campmate.data.model

import com.google.gson.annotations.SerializedName

data class Campsite(
    // (수정) 백엔드 Entity의 id가 보통 Long이므로 Int -> Long 변경 권장
    @SerializedName("id") // 백엔드 JSON 필드명과 일치시킴
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("imageUrl")
    val imageUrl: String,

    @SerializedName("rating")
    val rating: Float,

    //  이 캠핑장이 보유한 사이트(존) 목록
    @SerializedName("sites")
    val sites: List<CampsiteSite> = emptyList(), // 상세 정보 화면에서만 사용될 수 있음

    @SerializedName("address")
    val address: String?, // 'AdminEntity'의 'address'

    @SerializedName("adminPhoneNumber")
    val adminPhoneNumber: String?, // 'AdminEntity'의 'phoneNumber' (또는 다른 이름)

    // (추가) ReservationRequest DTO에 필요한 adminId
    // 🚨 백엔드 API(GET /api/zones/{id}) 응답에 이 필드가 포함되어야 합니다.
    @SerializedName("adminId")
    val adminId: Long
)