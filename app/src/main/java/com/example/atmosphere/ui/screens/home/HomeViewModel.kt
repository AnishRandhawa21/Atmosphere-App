package com.example.atmosphere.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atmosphere.data.model.Song
import com.example.atmosphere.data.repository.MusicRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

class HomeViewModel(
    private val repo: MusicRepository
) : ViewModel() {

    val songs = repo.songs
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // 🎧 FETCH MUSIC
    fun sendMood(text: String) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val response = repo.chat(text)

                repo.setSongs(response.songs)

                if (response.songs.isEmpty()) {
                    errorMessage = "No songs found"
                }

            } catch (e: Exception) {
                errorMessage = "Something went wrong"
            } finally {
                isLoading = false
            }
        }
    }

    // ❤️ FEEDBACK (LIKE / DISLIKE)
    fun sendFeedback(song: Song, liked: Boolean) {
        viewModelScope.launch {

            // 🔥 update shared state FIRST (instant UI update everywhere)
            repo.updateLike(song, liked)

            // 🌐 then call backend
            repo.sendFeedback(
                name = song.name,
                artist = song.artist,
                url = song.url,
                liked = liked,
                image = song.image ?: ""
            )
        }
    }
}