// com.forestteam.campmate.data.model.Auth.kt

package com.example.campmate.data.model
import com.google.gson.annotations.SerializedName

// 로그인 요청 Body
data class LoginRequest(
    @SerializedName("customerEmail") // JSON으로 보낼 때 사용할 key 이름
    val email: String,

    @SerializedName("customerPass")
    val pass: String
)

// 로그인 응답 Body
data class LoginResponse(
    val token: String,
    val user: User
)

// 회원가입 요청 Body
data class SignupRequest(
    @SerializedName("customerEmail")
    val email: String,

    @SerializedName("customerPass")
    val pass: String,

    @SerializedName("customerName")
    val name: String,

    @SerializedName("customerTel")
    val tel: String
)

// 유저 정보
data class User(
    val id: Int,
    val email: String,
    val name: String
)