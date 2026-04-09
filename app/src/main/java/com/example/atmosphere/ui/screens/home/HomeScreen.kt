package com.example.atmosphere.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.atmosphere.data.repository.MusicRepository
import com.example.atmosphere.ui.components.SongCard
import com.example.atmosphere.ui.navigation.Routes
import com.example.atmosphere.ui.theme.Serif
import com.example.atmosphere.utils.TokenManager

private val BgPage   = Color(0xFFF5F5F7)
private val BgCard   = Color(0xFFFFFFFF)
private val Primary  = Color(0xFF9575CD)
private val PrimaryL = Color(0xFFEDE7F6)
private val TextHero = Color(0xFF1A1A2E)
private val TextBody = Color(0xFF3D3D4E)
private val TextSub  = Color(0xFF9E9EA8)

@Composable
fun HomeScreen(
    navController: NavController,
    rootNavController: NavController,  // 🔥 ADD THIS
    repo: MusicRepository
) {
    val context      = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repo))

    var input by remember { mutableStateOf("") }
    val songs by viewModel.songs.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPage)
    ) {
        LazyColumn(
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
                    HomeIconBtn(
                        onClick = {
                            rootNavController.navigate(Routes.Login.route) {
                                popUpTo(Routes.Main.route) {
                                    inclusive = true
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.Logout, null, tint = TextSub, modifier = Modifier.size(17.dp))
                    }
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
                        "How are you\nfeeling today?",
                        color      = TextHero,
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 40.sp,
                        fontFamily = Serif
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Describe your vibe and we'll find the perfect soundtrack.",
                        color      = TextSub,
                        fontSize   = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            // ── Mood input ────────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(50), ambientColor = Color(0x14000000))
                            .clip(RoundedCornerShape(50))
                            .background(BgCard)
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value         = input,
                            onValueChange = { input = it },
                            singleLine    = true,
                            textStyle     = TextStyle(color = TextHero, fontSize = 14.sp),
                            cursorBrush   = SolidColor(Primary),
                            decorationBox = { inner ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 16.dp)
                                ) {
                                    if (input.isEmpty()) {
                                        Text("Describe your vibe...", color = TextSub, fontSize = 14.sp)
                                    }
                                    inner()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (input.isNotBlank() && !viewModel.isLoading) Primary
                                    else Primary.copy(alpha = 0.35f)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication        = null
                                ) { if (input.isNotBlank()) viewModel.sendMood(input) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(16.dp),
                                    color       = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Outlined.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(17.dp))
                            }
                        }
                    }
                }
            }

            // ── Section header ────────────────────────────────────────────────
            if (songs.isNotEmpty()) {
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
                            "Curated for your state",
                            color      = TextHero,
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Serif
                        )
                        Text("AI Generated", color = Primary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ── Empty state ───────────────────────────────────────────────────
            if (!viewModel.isLoading && songs.isEmpty()) {
                item {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp).clip(CircleShape).background(PrimaryL),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Headphones, null, tint = Primary.copy(0.7f), modifier = Modifier.size(30.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("No songs yet", color = TextBody, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("Describe your mood above", color = TextSub, fontSize = 13.sp)
                    }
                }
            }

            // ── Song cards — shared SongCard ──────────────────────────────────
            itemsIndexed(songs, key = { _, s -> s.url }) { _, song ->
                SongCard(
                    song          = song,
                    navController = navController,
                    onLike        = { viewModel.sendFeedback(song, !song.isLiked) }
                )
            }
        }
    }
}

@Composable
private fun HomeIconBtn(onClick: () -> Unit, content: @Composable () -> Unit) {
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