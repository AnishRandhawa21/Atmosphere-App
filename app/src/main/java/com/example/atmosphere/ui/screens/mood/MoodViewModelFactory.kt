package com.example.atmosphere.ui.screens.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.atmosphere.data.repository.MusicRepository

class MoodViewModelFactory(
    private val repo: MusicRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MoodViewModel(repo) as T
    }
}