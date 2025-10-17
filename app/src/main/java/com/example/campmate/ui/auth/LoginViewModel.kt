package com.example.campmate.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.TokenManager
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
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState

    // ✅ [수정] 파라미터 이름을 idInput -> customerId로 더 명확하게 변경
    fun login(customerId: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
                // ✅✅✅ [해결] LoginRequest의 파라미터 이름을 'id'에서 'customerId'로 수정합니다. ✅✅✅
                val response = apiService.login(LoginRequest(customerId = customerId, pass = pass))

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token

                    // 로그인 성공 시 받은 토큰을 TokenManager에 저장합니다.
                    tokenManager.saveToken(token)

                    _loginState.value = LoginUiState.Success(token)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "알 수 없는 에러"
                    _loginState.value = LoginUiState.Error("로그인 실패: ${response.code()} / $errorBody")
                }
            } catch (e: Exception) {
                _loginState.value = LoginUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}
