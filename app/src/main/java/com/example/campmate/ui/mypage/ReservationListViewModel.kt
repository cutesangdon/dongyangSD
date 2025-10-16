package com.example.campmate.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.ReservationRepository
import com.example.campmate.data.model.Reservation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ReservationListViewModel @Inject constructor(
    reservationRepository: ReservationRepository
) : ViewModel() {

    // ReservationRepository의 예약 목록을 실시간으로 관찰(구독)합니다.
    val reservations: StateFlow<List<Reservation>> = reservationRepository.reservations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}