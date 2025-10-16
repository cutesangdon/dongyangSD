package com.example.campmate.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.model.ReviewRequest
import com.example.campmate.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    // ✅✅✅ [수정됨] campsiteId 파라미터를 String이 아닌 Int 타입으로 받습니다. ✅✅✅
    fun submitReview(campsiteId: Int, rating: Int, content: String) {
        viewModelScope.launch {
            try {
                val request = ReviewRequest(
                    campsiteId = campsiteId,
                    rating = rating.toFloat(),
                    content = content
                )
                apiService.submitReview(request)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}