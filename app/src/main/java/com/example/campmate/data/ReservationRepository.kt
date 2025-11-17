package com.example.campmate.data

import android.R.attr.rating
import android.util.Log
import com.example.campmate.data.model.Campsite
import com.example.campmate.data.model.Reservation
import com.example.campmate.data.model.ReservationRequest
import com.example.campmate.data.model.Review
import com.example.campmate.data.model.ReviewRequest
import com.example.campmate.data.remote.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepository @Inject constructor(
    private val apiService: ApiService, // (수정) API 서비스 주입
    //25.11.10 KM 수정
    private val tokenManager: TokenManager
) {

    // --- 예약 관련 ---
    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations = _reservations.asStateFlow()

    /**
     * (수정) API를 호출하여 서버에 예약을 생성하는 함수 (suspend 함수로 변경)
     */
    suspend fun addReservation(campsite: Campsite, adults: Int, children: Int, startDateMillis: Long, endDateMillis: Long) {

        // 1. (추가) 서버에 보낼 DTO 생성
        // 🚨 중요: 백엔드(ReservationRequestDTO.java)는 adminsId를 요구합니다.
        // Campsite 데이터 클래스에 adminId 필드가 없다면 이 부분은 컴파일 에러가 발생합니다.
        // campsite.adminId 또는 다른 경로로 adminId를 가져와야 합니다.
        val request = ReservationRequest(
            campingZoneId = campsite.id,
            adminsId = campsite.adminId, // 🚨 이 필드를 campsite 모델에서 가져올 수 있어야 함
            checkIn = formatDate(startDateMillis),
            checkOut = formatDate(endDateMillis),
            adults = adults,
            children = children
            // siteName 등 DTO에 필요한 다른 필드가 있다면 추가
        )

        try {
            // 2. (추가) API 호출 (AuthInterceptor가 헤더를 자동으로 추가해 줌)
            val response = apiService.makeReservation(request)

            if (response.isSuccessful) {
                Log.d("ReservationRepo", "✅ 예약 성공")
                // 3. (추가) 예약 성공 시, 나의 예약 목록을 새로고침
                // TODO: 1L 대신 실제 로그인된 customerId를 DataStore 등에서 가져와야 함
                fetchMyReservations(1L)
            } else {
                Log.e("ReservationRepo", "❌ 예약 실패: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("ReservationRepo", "❌ 예약 중 네트워크 오류", e)
        }

        // (수정) 로컬에만 추가하던 코드는 삭제 (이제 서버 응답을 사용)
        // _reservations.update { currentList -> currentList + newReservation }
    }

    /**
     * (추가) 서버에서 "나의 예약 목록"을 불러오는 함수
     */
    suspend fun fetchMyReservations(customerId: Long) {
        try {
            val response = apiService.getMyReservations(customerId)
            if (response.isSuccessful) {
                // 3. 성공 시 StateFlow 업데이트
                _reservations.value = response.body() ?: emptyList()
                Log.d("ReservationRepo", "✅ 예약 목록 로드 성공: ${response.body()?.size}개")
            } else {
                Log.e("ReservationRepo", "❌ 예약 목록 로드 실패: ${response.code()}")
                _reservations.value = emptyList() // 실패 시 비워줌
            }
        } catch (e: Exception) {
            Log.e("ReservationRepo", "❌ 예약 목록 로드 중 네트워크 오류", e)
            _reservations.value = emptyList() // 실패 시 비워줌
        }
    }

    private fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    // --- 리뷰 관련 ---
    private val _myReviews = MutableStateFlow<List<Review>>(emptyList())
    val myReviews = _myReviews.asStateFlow()

    // 11.10 KM 수정
    /*
    fun addMyReview(campsiteName: String, rating: Float, content: String) {
        val newReview = Review(
            reviewId = (_myReviews.value.maxOfOrNull { it.reviewId } ?: 0) + 1,
            campsiteId = 0, // 임시 ID
            campsiteName = campsiteName,
            authorName = "나", // 지금은 작성자를 '나'로 고정
            rating = rating,
            content = content,
            imageUrls = emptyList(), // 이미지 URL은 비어있는 리스트로 전달
            createdAt = formatDate(System.currentTimeMillis()) // 현재 시간을 날짜 문자열로 변환하여 전달

     */
    /*
    25.11.10 KM 수정 (추가) 서버에서 현재 로그인된 사용자의 리뷰 목록을 불러온다.
     */
    suspend fun fetchMyReviews() {
        //1. 사용자 ID 가져오기
        val customerId = tokenManager.getUserId() ?: run {
            Log.e("ReviewRepo", "사용자 ID를 찾을 수 없습니다. 리뷰 로드 중단")
            _myReviews.value = emptyList()
            return
        }
        // 25.11.13 DH 수정 (ApiService - getMyReviews 수정)
        try {
            // 2. (추가) ApiService를 통해 서버에서 내 리뷰 목록 호출
            //getMyReviews(Long) 함수가 정의
            val response = apiService.getMyReviews(customerId)

            if (response.isSuccessful) {
                _myReviews.value = response.body() ?: emptyList()
                Log.d("ReviewRepo", "내 리뷰 목록 로드 성공: ${response.body()?.size}개")
            } else {
                Log.e("ReviewRepo", "내 리뷰 목록 로드 실패: ${response.code()}")
                _myReviews.value = emptyList()
            }
        } catch (e: Exception) {
            Log.e("ReviewRepo", "내 리뷰 목록 로드 중 네트워크 오류", e)
            _myReviews.value = emptyList()
        }
    }
    // 25.11.13 DH 수정
    suspend fun addMyReview(
        // 💡 Review 모델에 맞추기 위해 필요한 인자를 임시로 추가합니다.
        reservationId: Long,
        campingZoneId: Int,
//        customerId: Long, // customerId는 TokenManager에서 가져오므로 인자에서 제거
        rating: Float,
        content: String
    ) {
        // 1. (추가) 사용자 ID 가져오기
        val customerId = tokenManager.getUserId() ?: run {
            Log.e("ReviewRepo", "사용자 ID를 찾을 수 없습니다. 리뷰 등록 중단")
            return
        }
        // 2. (추가) 서버에 보낼 DTO 생성
        val request = ReviewRequest(
            reservationId = reservationId,
            campingZoneId = campingZoneId,
            customerId = customerId,
            rating = rating,
            content = content // ⬅️ DTO의 'coment' 필드에 할당
        )
        try {
            // 3. (추가) API 호출
            // ❗️ ApiService에 submitReview(ReviewRequest) 함수가 정의되어 있어야 합니다.
            val response = apiService.submitReview(request)

            if (response.isSuccessful) {
                Log.d("ReviewRepo", "리뷰 등록 성공")
                // 4. (추가) 리뷰 등록 성공 시, 내 리뷰 목록을 새로고침
                fetchMyReviews()
            } else {
                Log.e("ReviewRepo", "리뷰 등록 실패: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("ReviewRepo", "리뷰 등록 중 네트워크 오류", e)
        }
    }
}