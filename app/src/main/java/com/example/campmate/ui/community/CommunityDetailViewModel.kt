package com.example.campmate.ui.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.model.CommentPostRequest
import com.example.campmate.data.model.CommunityDetailResponse
import com.example.campmate.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PostDetailUiState {
    data object Loading : PostDetailUiState
    data class Success(val post: CommunityDetailResponse) : PostDetailUiState
    data class Error(val message: String) : PostDetailUiState
}

@HiltViewModel
class CommunityDetailViewModel @Inject constructor(
    private val apiService: ApiService,
    savedStateHandle: SavedStateHandle // 네비게이션으로 전달받은 인자(postId)를 받기 위해 필요
) : ViewModel() {

    // "postId"라는 이름으로 전달된 인자를 가져옴
    private val postId: Long = checkNotNull(savedStateHandle["postId"])

    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    // 사용자가 입력 중인 새 댓글 텍스트
    private val _newComment = MutableStateFlow("")
    val newComment: StateFlow<String> = _newComment.asStateFlow()

    init {
        loadPostDetail()
    }

    fun loadPostDetail() {
        viewModelScope.launch {
            _uiState.value = PostDetailUiState.Loading
            try {
                val response = apiService.getPostDetail(postId)
                if (response.isSuccessful) {
                    _uiState.value = PostDetailUiState.Success(response.body()!!)
                } else {
                    _uiState.value = PostDetailUiState.Error("게시글 로딩 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = PostDetailUiState.Error("네트워크 오류: ${e.message}")
            }
        }
    }

    fun onNewCommentChange(text: String) {
        _newComment.value = text
    }

    // 새 댓글 제출
    fun submitComment() {
        val commentText = _newComment.value
        if (commentText.isBlank()) return

        viewModelScope.launch {
            try {
                val request = CommentPostRequest(comment = commentText)
                val response = apiService.createComment(postId, request)

                if (response.isSuccessful) {
                    _newComment.value = "" // 입력창 비우기
                    loadPostDetail() // 댓글 목록 새로고침
                } else {
                    // TODO: 댓글 작성 실패 시 사용자에게 알림 (예: Toast)
                }
            } catch (e: Exception) {
                // TODO: 네트워크 오류 시 사용자에게 알림
            }
        }
    }
}