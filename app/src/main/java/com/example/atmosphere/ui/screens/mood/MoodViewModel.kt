package com.example.atmosphere.ui.screens.mood

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atmosphere.data.model.Song
import com.example.atmosphere.data.repository.MusicRepository
import kotlinx.coroutines.launch

class MoodViewModel(
    private val repo: MusicRepository
) : ViewModel() {

    var likedSongs by mutableStateOf<List<Song>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // 📥 LOAD LIKED SONGS
    fun loadLikedSongs() {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                likedSongs = repo.getLikedSongs().map {
                    it.copy(
                        isLiked = true,
                        image = it.image ?: ""
                    )
                }

                if (likedSongs.isEmpty()) {
                    errorMessage = "No liked songs yet"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // ❤️ UNLIKE SONG
    fun unlikeSong(song: Song) {
        viewModelScope.launch {
            try {
                repo.sendFeedback(
                    name = song.name,
                    artist = song.artist,
                    url = song.url,
                    image = song.image ?: "",
                    liked = false
                )

                // 🔥 FIX: update shared state
                repo.setSongs(
                    repo.songs.value.filter { it.url != song.url }
                )

                // remove from Mood UI
                likedSongs = likedSongs.filter {
                    it.url != song.url
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}