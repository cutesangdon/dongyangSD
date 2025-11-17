package com.example.campmate.ui.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.ReservationRepository
import com.example.campmate.data.TokenManager // 👈 (확인) TokenManager 주입
import com.example.campmate.data.model.Reservation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationListViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val tokenManager: TokenManager // 👈 (확인) TokenManager 주입
) : ViewModel() {

    // Repository의 예약 목록 StateFlow를 그대로 구독합니다.
    val reservations: StateFlow<List<Reservation>> = reservationRepository.reservations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * ViewModel이 생성될 때(화면이 열릴 때)
     * 서버에서 예약 목록을 불러오는 함수를 호출합니다.
     */
    init {
        fetchReservations()
    }

    private fun fetchReservations() {
        // (수정) Flow가 아니므로 viewModelScope.launch는 불필요하지만,
        // 어차피 fetchMyReservations가 suspend 함수이므로 launch는 유지합니다.
        viewModelScope.launch {

            // (수정) 1. TokenManager에서 customerId를 동기 함수로 가져옵니다.
            val customerId = tokenManager.getUserId() // 👈 customerIdFlow.firstOrNull() 대신 getUserId() 사용

            if (customerId != null && customerId > 0) {
                // 2. Repository의 API 호출 함수를 실행합니다.
                reservationRepository.fetchMyReservations(customerId)
            } else {
                // customerId가 없으면 로드 실패 (로그아웃 상태)
                Log.e("ReservationListVM", "Customer ID not found. Cannot fetch reservations.")
            }
        }
    }
}