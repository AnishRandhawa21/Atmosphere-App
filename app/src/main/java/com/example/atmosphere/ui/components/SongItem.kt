package com.example.atmosphere.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.atmosphere.data.model.Song

@Composable
fun SongItem(
    song: Song,
    onLike: () -> Unit,
    onDislike: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(song.name, style = MaterialTheme.typography.titleMedium)
            Text(song.artist, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(onClick = onLike) {
                    Text("👍")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onDislike) {
                    Text("👎")
                }
            }
        }
    }
}