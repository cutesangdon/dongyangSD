package com.example.campmate.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.ReservationRepository
import com.example.campmate.data.model.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MyReviewsViewModel @Inject constructor(
    reservationRepository: ReservationRepository
) : ViewModel() {

    // ✅ [수정됨] Repository의 리뷰 목록을 실시간으로 관찰하도록 코드를 유지합니다.
    // 이렇게 하면 ReservationRepository에서 가짜 데이터를 생성하므로, 여기서 따로 만들 필요가 없습니다.
    val myReviews: StateFlow<List<Review>> = reservationRepository.myReviews
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}