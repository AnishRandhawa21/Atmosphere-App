package com.example.atmosphere.ui.screens.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.atmosphere.data.repository.MusicRepository

class DiscoveryViewModelFactory(private val repo: MusicRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscoveryViewModel::class.java))
            return DiscoveryViewModel(repo) as T
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
