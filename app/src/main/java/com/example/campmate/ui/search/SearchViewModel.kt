
package com.example.campmate.ui.search

import androidx.lifecycle.ViewModel
import com.example.campmate.data.model.Campsite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    // 전체 캠핑장 목록 (가짜 데이터)
    private val _allCampsites = MutableStateFlow<List<Campsite>>(emptyList())

    // 사용자가 입력한 검색어
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // 사용자가 선택한 지역
    private val _selectedRegion = MutableStateFlow<String?>(null)
    val selectedRegion = _selectedRegion.asStateFlow()

    // 검색어와 지역에 따라 필터링된 최종 결과 목록
    val filteredCampsites = combine(_allCampsites, _searchQuery, _selectedRegion) { campsites, query, region ->
        campsites.filter { campsite ->
            val matchesQuery = campsite.name.contains(query, ignoreCase = true)
            val matchesRegion = region == null || campsite.address.startsWith(region)
            matchesQuery && matchesRegion
        }
    }

    init {
        // TODO: 나중에는 실제 API를 호출하여 전체 캠핑장 목록을 가져와야 합니다.
        _allCampsites.value = listOf(
            Campsite(1, "솔밭 캠핑장", "강원도 영월군", "https://i.imgur.com/tB0gE2j.jpeg", 4.5f),
            Campsite(2, "별빛 글램핑", "경기도 가평군", "https://i.imgur.com/azC4mD3.jpeg", 4.8f),
            Campsite(3, "바다향기 오토캠핑장", "충남 태안군", "https://i.imgur.com/8x0B0kM.jpeg", 4.3f),
            Campsite(4, "제주 돌담 캠핑", "제주도 제주시", "https://i.imgur.com/wAFf2kF.jpeg", 4.9f),
            Campsite(5, "숲속의 하루", "전북 무주군", "https://i.imgur.com/PbiIf32.jpeg", 4.6f),
            Campsite(6, "경기 캠핑파크", "경기도 포천시", "https://i.imgur.com/tB0gE2j.jpeg", 4.1f),
            Campsite(7, "강원 힐링캠프", "강원도 홍천군", "https://i.imgur.com/azC4mD3.jpeg", 4.7f)
        )
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onRegionSelected(region: String) {
        if (_selectedRegion.value == region) {
            _selectedRegion.value = null // 같은 지역을 다시 누르면 선택 해제
        } else {
            _selectedRegion.value = region
        }
    }
}