package com.example.campmate.data.remote

import com.example.campmate.data.model.*
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    // --- 사용자 인증 ---
    @POST("customer/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("customer/signup")
    suspend fun signup(@Body request: SignupRequest): Response<Unit>

    // --- 캠핑장 ---
    // 1030cdh
    @GET("api/zones/home") // 백엔드의 URL이 "home"이 맞는지 확인하세요.
    suspend fun getAllCampsites(): Response<List<AdminZoneGroup>>

    // ✅ [추가] 특정 ID의 캠핑장 상세 정보를 가져옵니다.
    @GET("api/zones/{id}")
    suspend fun getCampsiteDetail(@Path("id") campsiteId: Long): Response<Campsite>

    //25.11.14 KM 수정
    @GET("api/zones/search")
    suspend fun searchCampsites(
        @Query("keyword") keyword: String,
        @Query("region") region: String?
    ): Response<List<Campsite>>

    //11.10 수정 KM
    // --- 리뷰 ---
    @POST("reviews/submit") // 수정: cdh1028
    suspend fun submitReview(@Body reviewRequest: ReviewRequest): Response<Unit>

    // 25.11.14 DH
    @GET("reviews/my/{customerId}")
    suspend fun getMyReviews(@Path("customerId") customerId : Long): Response<List<Review>>

    // ✅✅✅ [핵심] ViewModel이 찾고 있던 'getCampsiteReviews' 함수입니다. ✅✅✅
    // (백엔드에 GET /reviews/campsite/{id} 주소가 필요합니다)
    // cdh1030 : 주소 수정
    @GET("api/zones/{id}/reviews")
    suspend fun getCampsiteReviews(@Path("id") campsiteId: Long): Response<List<Review>>

    // --- 체크리스트 ---
    @GET("/api/checklist/categories-with-items")
    suspend fun getChecklistPresets(): Response<List<ChecklistPresetItem>>

    @POST("/api/checklist/getChecklist/{customerId}")
    suspend fun getChecklist(@Path("customerId") customerId: Long): Response<List<ChecklistItemResponse>>

    @POST("/api/checklist/getAddItem/{customerId}")
    suspend fun addChecklistItem(
        @Path("customerId") customerId: Long,
        @Query("itemName") itemName: String
    ): Response<ChecklistItemResponse>

    @PUT("/api/checklist/{itemId}")
    suspend fun updateChecklistItem(
        @Path("itemId") itemId: Long,
        @Query("isChecked") isChecked: Boolean
    ): Response<ChecklistItemResponse>

    @DELETE("/api/checklist/{itemId}")
    suspend fun deleteChecklistItem(@Path("itemId") itemId: Long): Response<Unit>



    /**
     * 새로운 예약을 생성합니다.
     */
    @POST("api/reservations/make")
    suspend fun makeReservation(
        // (수정) AuthInterceptor가 토큰을 자동으로 헤더에 넣어주므로,
        // 수동으로 받던 @Header 파라미터를 제거해야 403 에러가 해결됩니다.
        // @Header("Authorization") token: String,
        @Body request: ReservationRequest
    ): Response<Unit>

    // (수정) 백엔드 Controller 및 SecurityConfig와 경로를 일치시킵니다.
    // (기존: "api/reservations/{custimerId}")
    @GET("api/reservations/customer/{customerId}")
    suspend fun getMyReservations(
        @Path("customerId") customerId: Long
    ): Response<List<Reservation>>

    @GET("customer/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): List<WeatherResponse>

}
