package com.example.campmate.data

import com.example.campmate.data.model.Campsite
import com.example.campmate.data.model.Reservation
import com.example.campmate.data.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepository @Inject constructor() {

    // --- 예약 관련 ---
    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations = _reservations.asStateFlow()

    fun addReservation(campsite: Campsite, adults: Int, children: Int, startDateMillis: Long, endDateMillis: Long, siteName: String) {
        val newReservation = Reservation(
            reservationId = "CM${System.currentTimeMillis()}",
            campsite = campsite,
            checkInDate = formatDate(startDateMillis),
            checkOutDate = formatDate(endDateMillis),
            adults = adults,
            children = children,
            selectedSiteName = siteName
        )
        _reservations.update { currentList -> currentList + newReservation }
    }

    private fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    // --- 리뷰 관련 ---
    private val _myReviews = MutableStateFlow<List<Review>>(emptyList())
    val myReviews = _myReviews.asStateFlow()

    fun addMyReview(campsiteName: String, rating: Float, content: String) {
        val newReview = Review(
            reviewId = (_myReviews.value.maxOfOrNull { it.reviewId } ?: 0) + 1,
            campsiteId = 0, // 임시 ID
            campsiteName = campsiteName,
            authorName = "나", // 지금은 작성자를 '나'로 고정
            rating = rating,
            content = content,
            // ✅✅✅ [수정됨] 빠져있던 파라미터들을 추가합니다. ✅✅✅
            imageUrls = emptyList(), // 이미지 URL은 비어있는 리스트로 전달
            createdAt = formatDate(System.currentTimeMillis()) // 현재 시간을 날짜 문자열로 변환하여 전달
        )
        _myReviews.update { currentList -> currentList + newReview }
    }
}