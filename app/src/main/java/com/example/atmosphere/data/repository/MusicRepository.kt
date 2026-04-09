package com.example.atmosphere.data.repository

import com.example.atmosphere.data.model.ChatResponse
import com.example.atmosphere.data.model.Song
import com.example.atmosphere.data.remote.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicRepository(private val api: ApiService) {

    // 🔥 SINGLE SOURCE OF TRUTH
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    fun setSongs(newSongs: List<Song>) {
        _songs.value = newSongs
    }

    fun updateLike(song: Song, liked: Boolean) {
        _songs.value = _songs.value.map {
            if (it.name == song.name && it.artist == song.artist) {
                it.copy(isLiked = liked)
            } else it
        }
    }

    // 🌐 API CALLS
    suspend fun getMusic(input: String) =
        api.getMusic(mapOf("input" to input))

    suspend fun sendFeedback(
        name: String,
        artist: String,
        url: String,
        liked: Boolean,
        image: String
    ) = api.sendFeedback(
        mapOf(
            "name" to name,
            "artist" to artist,
            "url" to url,
            "image" to image,
            "liked" to liked
        )
    )

    suspend fun getLikedSongs(): List<Song> {
        return api.getLikedSongs().songs
    }

    suspend fun searchSongs(query: String, limit: Int = 50): List<Song> {
        val response = api.searchSongs(query, limit)
        return response.songs
    }

    suspend fun getRecommendations(): List<Song> {
        return api.getRecommendations().songs
    }
    suspend fun chat(message: String): ChatResponse {
        return api.chat(mapOf("message" to message))
    }

    suspend fun getSongs(mood: String): List<Song> {
        return try {
            api.getSongsByMood(mood).songs
        } catch (e: Exception) {
            emptyList()
        }
    }
}