package com.example.atmosphere.data.remote

import com.example.atmosphere.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("login/")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("signup/")
    suspend fun signup(
        @Body request: LoginRequest
    ): SignupResponse

    @POST("mood/")
    suspend fun getMusic(
        @Body request: Map<String, String>
    ): Response<MusicResponse>

    @POST("feedback/")
    suspend fun sendFeedback(
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<Any>

    @GET("liked/")
    suspend fun getLikedSongs(): LikedResponse

    @GET("search/")
    suspend fun searchSongs(
        @Query("q")     query: String,
        @Query("limit") limit: Int = 50   // ← added; backend default kept at 50
    ): MusicResponse

    @GET("recommendations/")
    suspend fun getRecommendations(): MusicResponse

    @POST("api/chat/")
    suspend fun chat(@Body body: Map<String, String>): ChatResponse

    @GET("songs/")
    suspend fun getSongsByMood(
        @Query("mood") mood: String
    ): MusicResponse
}