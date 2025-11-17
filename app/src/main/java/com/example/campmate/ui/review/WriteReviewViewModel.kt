package com.example.campmate.ui.review

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.model.ReviewRequest
import com.example.campmate.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.campmate.data.TokenManager // 25.11.10 KM 수정
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow // 25.11.10 KM 수정
import kotlinx.coroutines.flow.asSharedFlow // 25.11.10 KM 수정

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val apiService: ApiService,
    //11.10 KM 수정
    private val tokenManager: TokenManager
) : ViewModel() {

    //25.11.10 KM 추가
    private val _submissionResult = MutableSharedFlow<Boolean>()
    val submissionResult = _submissionResult.asSharedFlow() // 화면에서 관찰할 Flow


    // ✅✅✅ [수정됨] campsiteId 파라미터를 String이 아닌 Int 타입으로 받습니다. ✅✅✅
    fun submitReview(reservationId: Long, campsiteId: Int, rating: Int, content: String) {
        viewModelScope.launch {
            try {
                //11.10 수정 KM
                val customerId = tokenManager.getUserId()
                    ?: run {
                        // ID를 가져올 수 없는 경우 (로그인 안 됨)
                        // Toast 메시지 등 사용자에게 알림을 띄우는 로직이 필요합니다.
                        println("Error: No customer ID found. User might not be logged in.")
                        return@launch
                    }


                val request = ReviewRequest(
                    campingZoneId = campsiteId,
                    customerId = customerId,
                    reservationId = reservationId,
                    rating = rating.toFloat(), // Int -> float 변환
                    content = content
                )

                //25.11.10 KM 추가 리뷰 API
                // 1. API 호출 및 응답 대기
                val response = apiService.submitReview(request)
                //2. 결과 확인 후 Flow를 통해 화면 전달
                if(response.isSuccessful){ // 성공
                    _submissionResult.emit(false) // 실패
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}