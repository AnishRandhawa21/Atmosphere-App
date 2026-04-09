package com.example.atmosphere.ui.screens.discovery

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atmosphere.data.model.Song
import com.example.atmosphere.data.repository.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SEARCH_LIMIT = 50   // ← change this to adjust result cap

class DiscoveryViewModel(
    private val repo: MusicRepository
) : ViewModel() {

    val songs = repo.songs

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var currentQuery by mutableStateOf("")

    private var searchJob: Job? = null  // debounce handle

    init {
        loadRecommendations()
    }

    // 🎯 RECOMMENDATIONS
    fun loadRecommendations() {
        viewModelScope.launch {
            try {
                isLoading    = true
                errorMessage = null

                val result = repo.getRecommendations()
                repo.setSongs(result)

            } catch (e: Exception) {
                errorMessage = "Failed to load recommendations"
            } finally {
                isLoading = false
            }
        }
    }

    // 🔍 SEARCH (debounced, with limit)
    fun searchSongs(query: String) {
        currentQuery = query

        if (query.isBlank()) {
            searchJob?.cancel()
            loadRecommendations()
            return
        }

        // Cancel previous pending search before starting a new one
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L)   // wait 300ms after last keystroke before firing

            try {
                isLoading    = true
                errorMessage = null

                val result = repo.searchSongs(query, limit = SEARCH_LIMIT)  // ← limit added
                repo.setSongs(result)

                if (result.isEmpty()) {
                    errorMessage = "No results found"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Network error"
            } finally {
                isLoading = false
            }
        }
    }

    // ❤️ LIKE
    fun toggleLike(song: Song) {
        viewModelScope.launch {
            repo.updateLike(song, !song.isLiked)

            repo.sendFeedback(
                name   = song.name,
                artist = song.artist,
                url    = song.url,
                liked  = !song.isLiked,
                image  = song.image ?: ""
            )
        }
    }
}