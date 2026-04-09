package com.example.atmosphere.ui.screens.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atmosphere.data.repository.AuthRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class AuthViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    var loginState by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            isLoading = true

            val result = repo.login(username, password)

            isLoading = false

            loginState = if (result.isSuccess) {
                "SUCCESS"
            } else {
                "ERROR"
            }
        }
    }
}