package com.example.atmosphere.ui.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.atmosphere.data.model.Song
import com.example.atmosphere.utils.PlayerManager

// ── Shared palette (matches HomeScreen / MoodScreen / DiscoveryScreen) ────────
private val BgCard    = Color(0xFFFFFFFF)
private val BgPage    = Color(0xFFF5F5F7)
private val Primary   = Color(0xFF9575CD)
private val PrimaryL  = Color(0xFFEDE7F6)
private val TextHero  = Color(0xFF1A1A2E)
private val TextSub   = Color(0xFF9E9EA8)

// ─────────────────────────────────────────────────────────────────────────────
// SongCard — single shared component used in Home, Mood, and Discovery screens
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SongCard(
    song: Song,
    navController: NavController,
    onLike: () -> Unit = {},
    // Pass false on MoodScreen where the heart is always filled and acts as unlike
    showUnlikeOnly: Boolean = false
) {
    val context = LocalContext.current
    Log.d("VIDEO_ID", song.videoId ?: "NULL")

    // Heart animation
    val heartScale by animateFloatAsState(
        targetValue   = if (song.isLiked) 1.22f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label         = "heartScale"
    )
    val heartTint by animateColorAsState(
        targetValue   = if (song.isLiked || showUnlikeOnly) Primary else TextSub.copy(0.5f),
        animationSpec = tween(200),
        label         = "heartTint"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x0A000000))
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
//            .clickable(
//                interactionSource = remember { MutableInteractionSource() },
//                indication        = null
//            ) {
//                PlayerManager.playSong(song, listOf(song))
//                navController.navigate("player")
//            }
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ── Album art ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryL)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(song.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = song.name,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.width(14.dp))

            // ── Song info ─────────────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    song.name,
                    color      = TextHero,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    song.artist,
                    color    = TextSub,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!song.duration.isNullOrBlank()) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        song.duration,
                        color    = TextSub.copy(0.7f),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // ── Play button ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryL)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null
                    ) {
                        PlayerManager.playSong(song, listOf(song))
                        navController.navigate("player")
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint     = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            // ── Heart button ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (song.isLiked || showUnlikeOnly) PrimaryL else BgPage
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = onLike
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (song.isLiked || showUnlikeOnly)
                        Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (song.isLiked) "Unlike" else "Like",
                    tint     = heartTint,
                    modifier = Modifier.size(17.dp).scale(heartScale)
                )
            }
        }
    }
}