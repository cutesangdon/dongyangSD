package com.example.campmate.data.model

import com.google.gson.annotations.SerializedName

// --- 응답(Response) DTO ---

/**
 * 댓글 응답 DTO (GET /api/community/{postId})
 */
data class CommentResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("comment") val comment: String,
    @SerializedName("authorNickname") val authorNickname: String,
    @SerializedName("createdDate") val createdDate: String // JSON 날짜/시간은 String으로 받는 것이 안전
)

/**
 * 게시글 목록 요약 응답 DTO (GET /api/community)
 */
data class CommunitySummaryResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("authorNickname") val authorNickname: String,
    @SerializedName("createdDate") val createdDate: String,
    @SerializedName("commentCount") val commentCount: Int
)

/**
 * 게시글 상세 응답 DTO (GET /api/community/{postId})
 */
data class CommunityDetailResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("authorNickname") val authorNickname: String,
    @SerializedName("createdDate") val createdDate: String,
    @SerializedName("comments") val comments: List<CommentResponse>
)

// --- 요청(Request) DTO ---

/**
 * 새 게시글 작성 요청 DTO (POST /api/community)
 */
data class CommunityPostRequest(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String
)

/**
 * 새 댓글 작성 요청 DTO (POST /api/community/{postId}/comments)
 */
data class CommentPostRequest(
    @SerializedName("comment") val comment: String
)