package com.example.campmate.data.remote

import com.example.campmate.data.model.Campsite
import com.example.campmate.data.model.ChecklistItemResponse
import com.example.campmate.data.model.ChecklistPresetItem
import com.example.campmate.data.model.LoginRequest
import com.example.campmate.data.model.LoginResponse
import com.example.campmate.data.model.ReviewRequest
import com.example.campmate.data.model.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("customer/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("customer/signup")
    suspend fun signup(@Body request: SignupRequest): Response<Unit>

    @GET("campsites")
    suspend fun getAllCampsites(): Response<List<Campsite>>

    @POST("reviews")
    suspend fun submitReview(@Body reviewRequest: ReviewRequest): Response<Unit>

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

}