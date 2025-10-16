package com.example.campmate.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.model.SignupRequest
import com.example.campmate.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SignupUiState {
    object Idle : SignupUiState()
    object Loading : SignupUiState()
    object Success : SignupUiState()
    data class Error(val message: String) : SignupUiState()
}

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _signupState = MutableStateFlow<SignupUiState>(SignupUiState.Idle)
    val signupState: StateFlow<SignupUiState> = _signupState

    fun signup(email: String, pass: String, name: String, tel: String) {
        viewModelScope.launch {
            _signupState.value = SignupUiState.Loading
            try {
                val request = SignupRequest(email, pass, name, tel)
                val response = apiService.signup(request)
                if (response.isSuccessful) {
                    _signupState.value = SignupUiState.Success
                } else {
                    val errorBody = response.errorBody()?.string() ?: "회원가입에 실패했습니다."
                    _signupState.value = SignupUiState.Error("실패: ${response.code()} / $errorBody")
                }
            } catch (e: Exception) {
                _signupState.value = SignupUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}