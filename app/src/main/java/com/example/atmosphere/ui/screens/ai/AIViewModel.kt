package com.example.atmosphere.ui.screens.ai

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atmosphere.data.model.Song
import com.example.atmosphere.data.repository.MusicRepository
import com.example.atmosphere.utils.PlayerManager
import kotlinx.coroutines.launch

class AIViewModel(
    private val repo: MusicRepository
) : ViewModel() {

    var messages by mutableStateOf(listOf<String>())
    var songs by mutableStateOf(listOf<Song>())
    var isLoading by mutableStateOf(false)

    fun sendMessage(text: String) {
        viewModelScope.launch {
            try {
                isLoading = true

                // 👤 User message
                messages = messages + "You: $text"

                // 🤖 Call backend (AI + YouTube + fallback)
                val response = repo.chat(text)

                // 🤖 AI reply
                messages = messages + "AI: ${response.reply}"

                // 🔥 Show if offline fallback used
                if (response.source == "offline") {
                    messages = messages + "AI: Showing offline songs ⚡"
                }

                // 🎧 Update songs
                songs = response.songs

                // ▶️ Auto-play first song
                if (songs.isNotEmpty()) {
                    PlayerManager.currentSong = songs[0]
                }

            } catch (e: Exception) {
                messages = messages + "AI: Something went wrong"
            } finally {
                isLoading = false
            }
        }
    }
}