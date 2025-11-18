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
    @GET("api/zones/home")
    suspend fun getAllCampsites(): Response<List<AdminZoneGroup>>

    @GET("api/zones/{id}")
    suspend fun getCampsiteDetail(@Path("id") campsiteId: Long): Response<Campsite>

    @GET("api/zones/search")
    suspend fun searchCampsites(
        @Query("keyword") keyword: String,
        @Query("region") region: String?
    ): Response<List<Campsite>>

    // --- 리뷰 ---
    @POST("reviews/submit")
    suspend fun submitReview(@Body reviewRequest: ReviewRequest): Response<Unit>

    @GET("reviews/my/{customerId}")
    suspend fun getMyReviews(@Path("customerId") customerId : Long): Response<List<Review>>

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

    @PUT("/api/checklist/{itemId}/quantity")
    suspend fun updateItemQuantity(
        @Path("itemId") itemId: Long,
        @Query("total") total: Int
    ): Response<ChecklistItemResponse>

    @DELETE("/api/checklist/{itemId}")
    suspend fun deleteChecklistItem(@Path("itemId") itemId: Long): Response<Unit>

    // --- 예약 ---
    @POST("api/reservations/make")
    suspend fun makeReservation(
        @Body request: ReservationRequest
    ): Response<Unit>

    @GET("api/reservations/customer/{customerId}")
    suspend fun getMyReservations(
        @Path("customerId") customerId: Long
    ): Response<List<Reservation>>

    // --- 날씨 ---
    @GET("customer/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): List<WeatherResponse>

    // --- 커뮤니티 ---
    @GET("/api/community")
    suspend fun getAllPosts(): Response<List<CommunitySummaryResponse>>

    @GET("/api/community/{postId}")
    suspend fun getPostDetail(@Path("postId") postId: Long): Response<CommunityDetailResponse>

    @POST("/api/community")
    suspend fun createPost(@Body request: CommunityPostRequest): Response<Unit>

    @POST("/api/community/{postId}/comments")
    suspend fun createComment(
        @Path("postId") postId: Long,
        @Body request: CommentPostRequest
    ): Response<CommentResponse>
}