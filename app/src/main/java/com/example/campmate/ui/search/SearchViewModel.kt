package com.example.campmate.ui.search

import androidx.lifecycle.ViewModel
import com.example.campmate.data.model.Campsite
// 11.14 KM 수정: CampsiteRepository 임포트
import com.example.campmate.data.CampsiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    // 11.14 KM 수정: Repository 주입
    private val campsiteRepository: CampsiteRepository
) : ViewModel() {

    // 사용자가 입력한 검색어
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // 사용자가 선택한 지역
    private val _selectedRegion = MutableStateFlow<String?>(null)
    val selectedRegion = _selectedRegion.asStateFlow()

    // 11.14 KM 수정: API 호출 로직으로 변경
    val filteredCampsites = combine(
        _searchQuery.debounce(300L), // 11.14 KM 수정: 디바운스 적용
        _selectedRegion // StateFlow는 distinctUntilChanged()가 불필요함
    ) { query, region ->
        // 11.14 KM 수정: 쿼리와 지역을 페어로 묶음
        Pair(query.trim(), region)
    }
        .flatMapLatest { (query, region) ->
            // 11.14 KM 수정: 쿼리가 비어있으면 API 호출 대신 빈 리스트 반환
            if (query.isEmpty() && region == null) {
                flowOf(emptyList())
            } else {
                // 11.14 KM 수정: Repository를 통해 API 호출 실행
                flow {
                    emit(campsiteRepository.searchCampsites(query, region))
                }
            }
        }

    // init { } 블록의 가짜 데이터 로직은 제거되었습니다.

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