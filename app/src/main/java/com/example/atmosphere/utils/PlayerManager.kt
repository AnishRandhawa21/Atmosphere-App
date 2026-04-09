package com.example.atmosphere.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.atmosphere.data.model.Song

object PlayerManager {

    var currentSong by mutableStateOf<Song?>(null)
    var playlist by mutableStateOf<List<Song>>(emptyList())
    var currentIndex by mutableStateOf(0)

    fun playSong(song: Song, list: List<Song>) {
        currentSong = song
        playlist = list
        currentIndex = list.indexOf(song)
    }

    fun next(): Song? {
        if (currentIndex < playlist.lastIndex) {
            currentIndex++
            currentSong = playlist[currentIndex]
        }
        return currentSong
    }

    fun previous(): Song? {
        if (currentIndex > 0) {
            currentIndex--
            currentSong = playlist[currentIndex]
        }
        return currentSong
    }

    fun hasNext(): Boolean {
        return currentIndex < playlist.lastIndex
    }

    fun hasPrevious(): Boolean {
        return currentIndex > 0
    }
}