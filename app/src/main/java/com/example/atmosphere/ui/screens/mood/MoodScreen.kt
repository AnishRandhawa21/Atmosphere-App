package com.example.atmosphere.ui.screens.mood

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.atmosphere.data.repository.MusicRepository
import com.example.atmosphere.ui.components.SongCard
import com.example.atmosphere.ui.theme.Serif

private val BgPage   = Color(0xFFF5F5F7)
private val BgCard   = Color(0xFFFFFFFF)
private val Primary  = Color(0xFF9575CD)
private val PrimaryL = Color(0xFFEDE7F6)
private val TextHero = Color(0xFF1A1A2E)
private val TextBody = Color(0xFF3D3D4E)
private val TextSub  = Color(0xFF9E9EA8)

@Composable
fun MoodScreen(
    navController: NavController,
    repo: MusicRepository
) {
    val viewModel: MoodViewModel = viewModel(factory = MoodViewModelFactory(repo))
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { viewModel.loadLikedSongs() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPage)
    ) {
        LazyColumn(
            state          = listState,
            modifier       = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {

            // ── Top bar ───────────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "Atmosphere",
                        color      = Primary,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Serif
                    )
                }
            }

            // ── Hero heading ──────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 8.dp, bottom = 24.dp)
                ) {
                    Text(
                        "Your Vibes",
                        color      = TextHero,
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 40.sp,
                        fontFamily = Serif
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "A sanctuary for the sounds you love.",
                        color    = TextSub,
                        fontSize = 13.sp
                    )
                }
            }

            // ── Loading ───────────────────────────────────────────────────────
            if (viewModel.isLoading) {
                item {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                    }
                }
            }

            // ── Error ─────────────────────────────────────────────────────────
            if (viewModel.errorMessage != null) {
                item {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.ErrorOutline, null, tint = Primary.copy(0.6f), modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(viewModel.errorMessage!!, color = TextSub, fontSize = 13.sp)
                    }
                }
            }

            // ── Section header ────────────────────────────────────────────────
            if (!viewModel.isLoading && viewModel.likedSongs.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            "Liked Songs",
                            color      = TextHero,
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Serif
                        )
                        Text("${viewModel.likedSongs.size} tracks", color = TextSub, fontSize = 12.sp)
                    }
                }
            }

            // ── Empty state ───────────────────────────────────────────────────
            if (!viewModel.isLoading && viewModel.likedSongs.isEmpty() && viewModel.errorMessage == null) {
                item {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp).clip(CircleShape).background(PrimaryL),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.FavoriteBorder, null, tint = Primary.copy(0.7f), modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("Nothing saved yet", color = TextBody, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("Like a song to see it here", color = TextSub, fontSize = 13.sp)
                    }
                }
            }

            // ── Liked cards — shared SongCard, showUnlikeOnly=true ────────────
            itemsIndexed(viewModel.likedSongs, key = { _, s -> s.url }) { index, song ->
                AnimatedMoodCard(index = index) {
                    SongCard(
                        song           = song,
                        navController  = navController,
                        showUnlikeOnly = true,
                        onLike         = { viewModel.unlikeSong(song) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedMoodCard(index: Int, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 55L)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(280)) + slideInVertically(tween(280)) { it / 3 }
    ) { content() }
}

@Composable
private fun MoodIconBtn(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(BgCard)
            .shadow(2.dp, CircleShape, ambientColor = Color(0x0A000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) { content() }
}