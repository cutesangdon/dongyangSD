package com.example.campmate.ui.home

import androidx.annotation.DrawableRes // (추가)
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.R // (추가) R.drawable.logo를 사용하기 위함
import com.example.campmate.data.model.AdminZoneGroup
import com.example.campmate.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// (기존) HomeUiState 정의 (변경 없음)
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val adminGroups: List<AdminZoneGroup>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

// (추가) 아이콘과 이름을 관리하는 데이터 클래스
data class CampingTheme(
    val name: String,
    @DrawableRes val iconRes: Int // 아이콘의 리소스 ID (예: R.drawable.ic_glamping)
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // (수정) 1. String 리스트 대신 CampingTheme 리스트로 변경
    // 🚨 중요: R.drawable.logo 부분을 실제 아이콘 리소스로 교체해야 합니다.
    val themes: List<CampingTheme> = listOf(
        CampingTheme("오토캠핑", R.drawable.logo),
        //CampingTheme("백패킹", R.drawable.logo),
        //CampingTheme("가족캠핑", R.drawable.logo),
        CampingTheme("반려견캠핑", R.drawable.logo),
        CampingTheme("산속", R.drawable.logo),
        CampingTheme("바다", R.drawable.logo),
        //CampingTheme("호수", R.drawable.logo),
        //CampingTheme("프리미엄", R.drawable.logo),

        )

    // (수정) 2. 선택된 테마를 '이름(String)'으로 관리 (변경 없음)
    private val _selectedTheme = MutableStateFlow<String?>(null)
    val selectedTheme: StateFlow<String?> = _selectedTheme.asStateFlow()

    init {
        fetchCampsites()
    }

    // (수정) 3. 테마 선택 이벤트 핸들러 (파라미터만 String으로 변경)
    fun onThemeSelected(themeName: String) {
        _selectedTheme.update { currentTheme ->
            if (currentTheme == themeName) null else themeName
        }

        fetchCampsites()
    }

    // (기존) 캠핑장 목록 로드 함수 (변경 없음)
    private fun fetchCampsites() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // (참고: 추후 백엔드에 테마 필터링 기능이 추가되면 _selectedTheme.value를 사용)
                val response = apiService.getAllCampsites()

                if (response.isSuccessful) {
                    _uiState.value = HomeUiState.Success(response.body() ?: emptyList())
                } else {
                    _uiState.value = HomeUiState.Error("캠핑장 목록을 불러오는 데 실패했습니다: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("네트워크 오류가 발생했습니다: ${e.message}")
            }
        }
    }
}