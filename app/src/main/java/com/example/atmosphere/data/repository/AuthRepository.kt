package com.example.atmosphere.data.repository

import com.example.atmosphere.data.model.AuthRequest
import com.example.atmosphere.data.model.LoginRequest
import com.example.atmosphere.data.model.LoginResponse
import com.example.atmosphere.data.remote.ApiService
import com.example.atmosphere.data.remote.RetrofitClient
import com.example.atmosphere.utils.TokenManager

class AuthRepository(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(username, password))

            tokenManager.saveToken(response.access)   // 🔥 SAVE JWT

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(username: String, password: String): Result<Unit> {
        return try {
            api.signup(LoginRequest(username, password))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}