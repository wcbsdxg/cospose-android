package com.cospose.gallery.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("api/auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun me(@Header("Authorization") token: String): Response<UserDto>

    // Images
    @GET("api/images")
    suspend fun getImages(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("sort") sort: String = "recommended"
    ): Response<ImageListResponse>

    @Multipart
    @POST("api/images")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("tagIds") tagIds: RequestBody?
    ): Response<ImageDto>

    @GET("api/images/{id}")
    suspend fun getImage(@Path("id") id: String): Response<ImageDto>

    @POST("api/images/{id}/like")
    suspend fun toggleLike(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Response<LikeResponse>

    @POST("api/images/{id}/rating")
    suspend fun submitRating(
        @Path("id") id: String,
        @Header("Authorization") token: String,
        @Body body: Map<String, Int>
    ): Response<Unit>

    @GET("api/images/{id}/comments")
    suspend fun getComments(@Path("id") id: String): Response<List<CommentDto>>

    @POST("api/images/{id}/comments")
    suspend fun addComment(
        @Path("id") id: String,
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<CommentDto>

    @GET("api/images/{id}/related")
    suspend fun getRelated(@Path("id") id: String): Response<List<ImageDto>>

    @POST("api/images/{id}/classify")
    suspend fun classifyImage(
        @Path("id") id: String,
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<ClassifyResponse>

    // Tags
    @GET("api/tags")
    suspend fun getTags(@Query("q") query: String? = null): Response<List<TagDto>>

    // Search
    @GET("api/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("mode") mode: String = "keyword",
        @Query("limit") limit: Int = 50
    ): Response<SearchResponse>

    @Multipart
    @POST("api/search")
    suspend fun searchByImage(
        @Part file: MultipartBody.Part
    ): Response<SearchResponse>

    // Boards
    @GET("api/boards")
    suspend fun getBoards(@Header("Authorization") token: String): Response<List<BoardDto>>

    @POST("api/boards")
    suspend fun createBoard(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<BoardDto>

    // Users
    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: String): Response<UserProfileDto>

    // AI — server-side CLIP
    @Multipart
    @POST("api/ai/classify")
    suspend fun classifyImageDirect(
        @Part file: MultipartBody.Part
    ): Response<AIclassifyResponse>

    @Multipart
    @POST("api/ai/embed/image")
    suspend fun embedImage(
        @Part file: MultipartBody.Part
    ): Response<EmbedResponse>

    @GET("api/ai/embed/text")
    suspend fun embedText(
        @Query("text") text: String
    ): Response<EmbedResponse>

    @Multipart
    @POST("api/ai/search/image")
    suspend fun imageSearch(
        @Part file: MultipartBody.Part
    ): Response<SearchResponse>

    // Sync
    @POST("api/sync/push")
    suspend fun syncPush(
        @Header("Authorization") token: String,
        @Body body: SyncPushRequest
    ): Response<SyncPushResponse>

    @GET("api/sync/pull")
    suspend fun syncPull(
        @Header("Authorization") token: String,
        @Query("since") since: Long
    ): Response<SyncPullResponse>

    @Multipart
    @POST("api/sync/images")
    suspend fun syncImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("imageId") imageId: RequestBody
    ): Response<SyncImageResponse>
}

// DTOs
data class AuthResponse(val token: String, val user: UserDto)
data class UserDto(val id: String, val name: String?, val email: String?, val role: String, val avatar: String?, val bio: String?)
data class ImageDto(val id: String, val title: String, val description: String?, val userId: String, val userName: String?, val width: Int, val height: Int, val mimeType: String, val likesCount: Int, val commentsCount: Int, val ratingAvg: Float, val ratingCount: Int, val score: Float, val createdAt: String, val tags: List<TagDto>?)
data class TagDto(val id: String, val name: String, val category: String, val imageCount: Int?)
data class CommentDto(val id: String, val userId: String, val userName: String?, val content: String, val createdAt: String)
data class BoardDto(val id: String, val name: String, val description: String?, val isPublic: Boolean, val imageCount: Int?)
data class UserProfileDto(val id: String, val name: String?, val bio: String?, val avatar: String?, val imageCount: Int, val images: List<ImageDto>?)
data class ImageListResponse(val images: List<ImageDto>, val total: Int, val page: Int, val totalPages: Int)
data class SearchResponse(val images: List<ImageDto>, val total: Int)
data class LikeResponse(val liked: Boolean, val likesCount: Int)
data class ClassifyResponse(val tags: List<ClassifyTag>)
data class ClassifyTag(val label: String, val score: Float, val category: String)
data class AIclassifyResponse(val categories: List<AICategoryResult>)
data class AICategoryResult(val name: String, val results: List<AILabelResult>)
data class AILabelResult(val label: String, val score: Float, val similarity: Float)
data class EmbedResponse(val embedding: List<Float>, val dimensions: Int)
data class SyncPushRequest(val images: List<SyncImageDto>, val comments: List<SyncCommentDto>, val ratings: List<SyncRatingDto>)
data class SyncPushResponse(val synced: Int)
data class SyncPullResponse(val images: List<ImageDto>, val since: Long)
data class SyncImageResponse(val serverId: String, val url: String)
data class SyncImageDto(val localId: String, val title: String, val description: String?)
data class SyncCommentDto(val localId: String, val imageId: String, val content: String)
data class SyncRatingDto(val imageId: String, val score: Int)
