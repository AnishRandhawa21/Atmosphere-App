package com.example.atmosphere.data.model

data class MusicResponse(
    val input: String,
    val search_query: String,
    val songs: List<Song>,
    val source: String

)