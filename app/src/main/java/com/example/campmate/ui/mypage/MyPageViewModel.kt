package com.example.campmate.ui.mypage

import androidx.lifecycle.ViewModel
import com.example.campmate.data.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    // 로그아웃 시 호출될 함수입니다.
    fun logout() {
        // TokenManager를 사용해 저장된 토큰을 삭제합니다.
        tokenManager.clearToken()
    }
}