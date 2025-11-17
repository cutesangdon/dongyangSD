package com.example.campmate.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.model.CommunityPostRequest
import com.example.campmate.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WritePostViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    // 글쓰기 결과를 1회성 이벤트로 UI에 전달하기 위해 SharedFlow 사용
    private val _postResult = MutableSharedFlow<Boolean>()
    val postResult = _postResult.asSharedFlow()

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
    }

    fun onContentChange(newContent: String) {
        _content.value = newContent
    }

    fun submitPost() {
        viewModelScope.launch {
            if (title.value.isBlank() || content.value.isBlank()) {
                _postResult.emit(false) // 제목 또는 내용이 비어있음
                return@launch
            }

            try {
                val request = CommunityPostRequest(title = _title.value, content = _content.value)
                val response = apiService.createPost(request)
                _postResult.emit(response.isSuccessful)
            } catch (e: Exception) {
                _postResult.emit(false)
            }
        }
    }
}