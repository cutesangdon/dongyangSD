package com.example.campmate.ui.detail

import android.util.Log
import android.util.Log.e
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.model.Campsite
import com.example.campmate.data.model.CampsiteSite
import com.example.campmate.data.model.ReservationRequest
import com.example.campmate.data.model.Review
import com.example.campmate.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow // (추가)
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow // (추가)
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CampsiteDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val apiService: ApiService
) : ViewModel() {

    private val _campsite = MutableStateFlow<Campsite?>(null)
    val campsite: StateFlow<Campsite?> = _campsite

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // (추가) 1. 예약 결과를 Screen에 알리기 위한 '이벤트 채널'
    private val _reservationResult = MutableSharedFlow<Boolean>()
    val reservationResult = _reservationResult.asSharedFlow()


    init {
        val campsiteId: Long = (savedStateHandle.get<Int>("campsiteId") ?: 0).toLong()
        if (campsiteId > 0) { // 0일 경우 로드하지 않음
            fetchAllDetails(campsiteId)
        } else {
            _error.value = "유효하지 않은 캠핑장 ID입니다."
            _isLoading.value = false
        }
    }

    private fun fetchAllDetails(campsiteId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // 두 API 호출을 동시에 (또는 순차적으로) 실행
                fetchCampsiteDetails(campsiteId)
                fetchReviews(campsiteId)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "데이터 로딩 중 오류 발생: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    // fetchCampsiteDetails 구현
    private suspend fun fetchCampsiteDetails(campsiteId: Long) {

        val response = apiService.getCampsiteDetail(campsiteId)
        if (response.isSuccessful) {
            _campsite.value = response.body()
        } else {
            throw Exception("캠핑장 정보 로드 실패: ${response.code()}")
        }
    }

    // fetchReviews 구현
    private suspend fun fetchReviews(campsiteId: Long) {
        val response = apiService.getCampsiteReviews(campsiteId)
        if (response.isSuccessful) {
            _reviews.value = response.body() ?: emptyList()
        } else {
            throw Exception("리뷰 정보 로드 실패: ${response.code()}")
        }
    }




    // 날짜 포맷 함수
    private fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    /**
     * ✅ [수정됨] API를 호출하여 예약합니다.
     */
    fun makeReservation(
        // (수정) 2. AuthInterceptor가 토큰을 처리하므로, authToken 파라미터를 제거합니다.
        // authToken: String,
        adults: Int,
        children: Int,
        startDate: Long,
        endDate: Long,
        site: CampsiteSite
    ) {
        _campsite.value?.let { currentCampsite ->
            viewModelScope.launch {
                try {
                    // (주석 제거)

                    // 1. 서버로 보낼 '예약 요청' 데이터
                    val request = ReservationRequest(
                        // (수정) 3. [중요] 캠핑장 ID가 아닌 관리자 ID를 전달해야 합니다.
                        adminsId = currentCampsite.adminId,
                        campingZoneId = site.siteId, // 🚨 CampsiteSite에 siteId가 있어야 함
                        checkIn = formatDate(startDate),
                        checkOut = formatDate(endDate),
                        adults = adults,
                        children = children
                    )

                    // 2. 실제 API 호출
                    // (수정) 4. 'authToken' 파라미터를 제거하고 'request'만 전달합니다.
                    val response = apiService.makeReservation(request)

                    if (response.isSuccessful) {
                        Log.d("CampsiteDetailVM", "✅ 예약 성공")
                        // (수정) 5. 성공 시 Screen에 'true' 이벤트 발행
                        _reservationResult.emit(true)
                    } else {
                        Log.e("CampsiteDetailVM", "❌ 예약 실패: ${response.code()} ${response.message()}")
                        // (수정) 6. 실패 시 Screen에 'false' 이벤트 발행
                        _reservationResult.emit(false)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("CampsiteDetailVM", "❌ 예약 중 네트워크 오류", e)
                    // (수정) 6. 실패 시 Screen에 'false' 이벤트 발행
                    _reservationResult.emit(false)
                }
            }
        }
    }
}