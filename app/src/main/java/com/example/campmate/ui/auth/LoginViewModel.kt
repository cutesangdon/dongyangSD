package com.example.campmate.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.TokenManager // ✅ TokenManager import
import com.example.campmate.data.model.LoginRequest
import com.example.campmate.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 로그인 화면의 UI 상태를 정의합니다 (초기, 로딩중, 성공, 실패)
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val token: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
// ✅ TokenManager를 주입받도록 생성자를 수정합니다.
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState

    fun login(email: String, pass: String) {
        // 임시 로그인 코드는 삭제하고, 실제 API만 호출합니다.
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
                val response = apiService.login(LoginRequest(email = email, pass = pass))

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token

                    // ✅✅✅ [핵심] 로그인 성공 시 받은 토큰을 TokenManager에 저장합니다. ✅✅✅
                    tokenManager.saveToken(token)

                    _loginState.value = LoginUiState.Success(token)
                } else {
                    val errorBody = response.errorBody()?.string()
                    _loginState.value = LoginUiState.Error("로그인 실패: ${response.code()} / $errorBody")
                }
            } catch (e: Exception) {
                _loginState.value = LoginUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}