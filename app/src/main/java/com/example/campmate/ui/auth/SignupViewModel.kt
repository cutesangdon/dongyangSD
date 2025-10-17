package com.example.campmate.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.model.SignupRequest
import com.example.campmate.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 회원가입 UI 상태 (초기, 로딩중, 성공, 실패)
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

    // --- UI State ---
    private val _signupState = MutableStateFlow<SignupUiState>(SignupUiState.Idle)
    val signupState: StateFlow<SignupUiState> = _signupState.asStateFlow()

    // --- 회원가입 데이터 관리 ---
    private val _customerId = MutableStateFlow("")
    val customerId: StateFlow<String> = _customerId.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _style = MutableStateFlow("")
    val style: StateFlow<String> = _style.asStateFlow()

    private val _background = MutableStateFlow("")
    val background: StateFlow<String> = _background.asStateFlow()

    private val _type = MutableStateFlow("")
    val type: StateFlow<String> = _type.asStateFlow()

    // --- 데이터 업데이트 함수 ---
    fun onCustomerIdChange(value: String) { _customerId.value = value }
    fun onPasswordChange(value: String) { _password.value = value }
    fun onEmailChange(value: String) { _email.value = value }
    fun onNameChange(value: String) { _name.value = value }
    fun onStyleChange(value: String) { _style.value = value }
    fun onBackgroundChange(value: String) { _background.value = value }
    fun onTypeChange(value: String) { _type.value = value }


    // --- 최종 회원가입 요청 함수 ---
    fun signup() {
        viewModelScope.launch {
            _signupState.value = SignupUiState.Loading
            try {
                val request = SignupRequest(
                    customerId = _customerId.value,
                    pass = _password.value,
                    email = _email.value,
                    name = _name.value,
                    style = _style.value,
                    background = _background.value,
                    type = _type.value
                )
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
