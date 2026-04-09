package com.example.atmosphere.ui.screens.player

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.atmosphere.ui.theme.Serif
import com.example.atmosphere.utils.PlayerManager
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

private val BgPage   = Color(0xFFF5F5F7)
private val BgCard   = Color(0xFFFFFFFF)
private val Primary  = Color(0xFF9575CD)
private val PrimaryL = Color(0xFFEDE7F6)
private val TextHero = Color(0xFF1A1A2E)
private val TextSub  = Color(0xFF9E9EA8)

@Composable
fun PlayerScreen() {
    val song    = PlayerManager.currentSong
    val context = LocalContext.current

    if (song == null) {
        Box(
            modifier         = Modifier.fillMaxSize().background(BgPage),
            contentAlignment = Alignment.Center
        ) {
            Text("No song selected", color = TextSub, fontSize = 14.sp)
        }
        return
    }

    var hasError by remember { mutableStateOf(false) }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .background(BgPage)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(16.dp))

        // ── Top label ─────────────────────────────────────────────────────────
        Text(
            "Now Playing",
            color         = TextSub,
            fontSize      = 11.sp,
            fontWeight    = FontWeight.SemiBold,
            letterSpacing = 1.4.sp
        )

        Spacer(Modifier.height(28.dp))

        // ── Album art / YouTube player ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(16.dp, RoundedCornerShape(28.dp), ambientColor = Color(0x18000000))
                .clip(RoundedCornerShape(28.dp))
                .background(PrimaryL)
        ) {
            if (!hasError && song.videoId != null) {
                AndroidView(
                    factory = { ctx ->
                        val playerView = YouTubePlayerView(ctx)
                        playerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                youTubePlayer.loadVideo(song.videoId, 0f)
                            }
                            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                                hasError = true
                            }
                        })
                        playerView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Fallback — show album art
                AsyncImage(
                    model              = song.image,
                    contentDescription = song.name,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── Song info ─────────────────────────────────────────────────────────
        Text(
            song.name,
            color      = TextHero,
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Serif,
            maxLines   = 2,
            overflow   = TextOverflow.Ellipsis,
            textAlign  = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            song.artist,
            color    = TextSub,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(36.dp))

        // ── Watch on YouTube button ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(4.dp, RoundedCornerShape(50), ambientColor = Color(0x0A000000))
                .clip(RoundedCornerShape(50))
                .background(BgCard)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null
                ) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(song.url))
                    context.startActivity(intent)
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Outlined.OpenInNew, null, tint = Primary, modifier = Modifier.size(16.dp))
                Text(
                    "Watch on YouTube",
                    color      = Primary,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // ── Fallback error hint ───────────────────────────────────────────────
        if (hasError) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Playback unavailable in-app — use the button above.",
                color      = TextSub,
                fontSize   = 12.sp,
                textAlign  = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}