package com.example.campmate.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.campmate.data.ReservationRepository
import com.example.campmate.data.model.Campsite
import com.example.campmate.data.model.CampsiteSite
import com.example.campmate.data.model.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CampsiteDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _campsite = MutableStateFlow<Campsite?>(null)
    val campsite: StateFlow<Campsite?> = _campsite

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    init {
        val campsiteId = savedStateHandle.get<Int>("campsiteId")
        if (campsiteId != null) {
            _campsite.value = getFakeCampsiteById(campsiteId)
            fetchReviews(campsiteId)
        }
    }

    fun makeReservation(adults: Int, children: Int, startDate: Long, endDate: Long, siteName: String) {
        _campsite.value?.let { currentCampsite ->
            reservationRepository.addReservation(currentCampsite, adults, children, startDate, endDate, siteName)
        }
    }

    private fun fetchReviews(campsiteId: Int) {
        // TODO: 나중에는 실제 API를 호출해야 합니다.
        // ✅✅✅ [수정됨] 가짜 리뷰 데이터에 campsiteName을 추가합니다. ✅✅✅
        _reviews.value = listOf(
            Review(101, campsiteId, "솔밭 캠핑장", "캠핑고수", 5f, "시설 깨끗하고 사장님도 친절하세요!", emptyList(), "2025-09-15"),
            Review(102, campsiteId, "솔밭 캠핑장", "초보캠퍼", 4f, "뷰가 정말 좋아요.", emptyList(), "2025-09-12"),
            Review(103, campsiteId, "솔밭 캠핑장", "감성캠퍼", 5f, "인생샷 찍기 좋은 곳!", emptyList(), "2025-09-11")
        )
    }

    private fun getFakeCampsiteById(id: Int): Campsite {
        val fakeSites = listOf(
            CampsiteSite("A-1", "데크존 A-1", 50000),
            CampsiteSite("A-2", "데크존 A-2", 50000),
            CampsiteSite("B-1", "파쇄석 B-1", 45000),
            CampsiteSite("G-1", "글램핑 G-1 (대형)", 150000)
        )
        val fakeCampsites = listOf(
            Campsite(1, "솔밭 캠핑장", "강원도 영월군", "https://i.imgur.com/tB0gE2j.jpeg", 4.5f, fakeSites),
            Campsite(2, "별빛 글램핑", "경기도 가평군", "https://i.imgur.com/azC4mD3.jpeg", 4.8f, fakeSites.drop(2)),
            Campsite(3, "바다향기 오토캠핑장", "충남 태안군", "https://i.imgur.com/8x0B0kM.jpeg", 4.3f, fakeSites),
            Campsite(4, "제주 돌담 캠핑", "제주시 애월읍", "https://i.imgur.com/wAFf2kF.jpeg", 4.9f, fakeSites.take(2)),
            Campsite(5, "숲속의 하루", "전북 무주군", "https://i.imgur.com/PbiIf32.jpeg", 4.6f, fakeSites)
        )
        return fakeCampsites.find { it.id == id }!!
    }
}