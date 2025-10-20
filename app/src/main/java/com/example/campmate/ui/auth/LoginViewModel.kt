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

    fun login(customerId: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
                val response = apiService.login(LoginRequest(email = customerId, pass = pass))

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    tokenManager.saveAuthData(token = loginResponse.token, userId = loginResponse.id)

                    _loginState.value = LoginUiState.Success(loginResponse.token)
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

