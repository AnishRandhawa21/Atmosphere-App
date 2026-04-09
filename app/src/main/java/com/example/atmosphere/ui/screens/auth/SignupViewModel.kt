package com.example.atmosphere.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atmosphere.data.repository.AuthRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

class SignupViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    var signupState by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun signup(username: String, password: String) {
        viewModelScope.launch {
            isLoading = true

            val result = repo.signup(username, password)

            isLoading = false

            signupState = if (result.isSuccess) {
                "SUCCESS"
            } else {
                "ERROR"
            }
        }
    }
}