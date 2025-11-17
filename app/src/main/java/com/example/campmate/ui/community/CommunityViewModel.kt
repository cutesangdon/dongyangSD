package com.example.campmate.ui.community

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.model.CommunitySummaryResponse
import com.example.campmate.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CommunityUiState {
    data object Loading : CommunityUiState
    data class Success(val posts: List<CommunitySummaryResponse>) : CommunityUiState
    data class Error(val message: String) : CommunityUiState
}

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommunityUiState>(CommunityUiState.Loading)
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    init {
        // ✅ [수정] ViewModel 생성 시 자동 로드를 제거합니다.
        // loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = CommunityUiState.Loading
            try {
                val response = apiService.getAllPosts()
                if (response.isSuccessful) {
                    _uiState.value = CommunityUiState.Success(response.body() ?: emptyList())
                } else {
                    _uiState.value = CommunityUiState.Error("게시글 로딩 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = CommunityUiState.Error("네트워크 오류: ${e.message}")
            }
        }
    }
}