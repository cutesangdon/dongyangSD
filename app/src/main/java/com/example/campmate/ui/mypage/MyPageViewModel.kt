package com.example.campmate.ui.mypage

import androidx.lifecycle.ViewModel
import com.example.campmate.data.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    fun logout() {
        tokenManager.clearAuthData()
    }
}
