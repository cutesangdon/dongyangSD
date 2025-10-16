// Reservation.kt

package com.example.campmate.data.model

data class Reservation(
    val reservationId: String,
    val campsite: Campsite,
    val checkInDate: String,
    val checkOutDate: String,
    val adults: Int,
    val children: Int,
    val selectedSiteName: String // (추가) 예약한 사이트 이름
)