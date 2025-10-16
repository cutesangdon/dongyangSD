package com.example.campmate.data.remote

import com.example.campmate.data.model.Campsite
import com.example.campmate.data.model.LoginRequest
import com.example.campmate.data.model.LoginResponse
import com.example.campmate.data.model.ReviewRequest
import com.example.campmate.data.model.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("customer/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("customer/signup")
    suspend fun signup(@Body request: SignupRequest): Response<Unit>

    // ✅ [수정됨] 추천 API 대신, 모든 캠핑장 목록을 가져오는 API를 호출합니다.
    @GET("campsites")
    suspend fun getAllCampsites(): Response<List<Campsite>>

    @POST("reviews")
    suspend fun submitReview(@Body reviewRequest: ReviewRequest): Response<Unit>
}