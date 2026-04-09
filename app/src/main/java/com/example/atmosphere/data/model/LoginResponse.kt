package com.example.atmosphere.data.model

data class LoginResponse(
    val access: String,
    val refresh: String,
    val username: String
)