package com.example.campmate.data.model
import com.google.gson.annotations.SerializedName

// 로그인 요청 Body
data class LoginRequest(
    @SerializedName("customerId")
    val customerId: String,

    @SerializedName("password")
    val pass: String
)

// 로그인 응답 Body
data class LoginResponse(
    @SerializedName("userName")
    val name: String,

    @SerializedName("accessToken")
    val token: String
)

data class SignupRequest(
    @SerializedName("customerId")
    val customerId: String,

    @SerializedName("password")
    val pass: String,

    @SerializedName("email")
    val email: String,

    // DB의 customers_name은 닉네임으로 사용됩니다.
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
