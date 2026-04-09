package com.example.atmosphere.data.model

data class Song(
    val name: String,
    val artist: String,
    val url: String,
    val image: String? = null,

    // ✅ YouTube playback
    val videoId: String? = null,

    // ✅ Optional metadata
    val duration: String? = null,

    // ❤️ UI state
    var isLiked: Boolean = false
)

data class ChatResponse(
    val reply: String,
    val songs: List<Song>,
    val source: String
)