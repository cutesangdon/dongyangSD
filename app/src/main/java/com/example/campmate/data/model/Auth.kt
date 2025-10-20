package com.example.campmate.data.model
import com.google.gson.annotations.SerializedName

// 로그인 요청 Body
data class LoginRequest(
    @SerializedName("customerId")
    val email: String,

    @SerializedName("customerPassword")
    val pass: String
)

// 로그인 응답 Body
data class LoginResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("userName")
    val name: String,
    @SerializedName("token")
    val token: String
)

// 회원가입 요청 Body
data class SignupRequest(
    @SerializedName("customerId")
    val customerId: String,

    @SerializedName("password")
    val pass: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("nickname")
    val name: String,

    @SerializedName("customersStyle")
    val style: String,

    @SerializedName("customersBackground")
    val background: String,

    @SerializedName("customersType")
    val type: String,

    val provider: String = "NORMAL"
)

