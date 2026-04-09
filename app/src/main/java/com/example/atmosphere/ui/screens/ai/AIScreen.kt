package com.example.atmosphere.ui.screens.ai

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.atmosphere.data.remote.RetrofitClient
import com.example.atmosphere.data.repository.MusicRepository
import com.example.atmosphere.ui.theme.Serif
import com.example.atmosphere.utils.PlayerManager

// ── Palette — unified with all other screens ──────────────────────────────────
private val BgPage      = Color(0xFFF5F5F7)
private val BgCard      = Color(0xFFFFFFFF)
private val Primary     = Color(0xFF9575CD)
private val PrimaryL    = Color(0xFFEDE7F6)
private val UserBubble  = Color(0xFF9575CD)
private val AiBubble    = Color(0xFFFFFFFF)
private val TextHero    = Color(0xFF1A1A2E)
private val TextBody    = Color(0xFF3D3D4E)
private val TextSub     = Color(0xFF9E9EA8)

// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AIScreen(navController: NavController) {

    val context   = LocalContext.current
    val api       = remember { RetrofitClient.getClient(context) }
    val repo      = remember { MusicRepository(api) }
    val viewModel = remember { AIViewModel(repo) }

    var input     by remember { mutableStateOf("") }
    val listState  = rememberLazyListState()

    // Auto-scroll to latest message
    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty())
            listState.animateScrollToItem(viewModel.messages.size - 1)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPage)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {

            // ── Top bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryL),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.AutoAwesome, null, tint = Primary, modifier = Modifier.size(17.dp))
                }
                Column {
                    Text(
                        "AI Assistant",
                        color      = TextHero,
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Serif
                    )
                    Text("Powered by Atmosphere", color = TextSub, fontSize = 11.sp)
                }
            }

            // ── Chat area ─────────────────────────────────────────────────────
            LazyColumn(
                state               = listState,
                modifier            = Modifier.weight(1f),
                contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // Welcome bubble if no messages
                if (viewModel.messages.isEmpty()) {
                    item {
                        AiBubble(
                            text = "Hi! Tell me how you're feeling and I'll find the perfect music for your mood 🎵"
                        )
                    }
                }

                // Chat messages
                items(viewModel.messages) { msg ->
                    val isUser = msg.startsWith("You:")
                    val text   = msg.removePrefix("You:").removePrefix("AI:").trim()

                    if (isUser) {
                        UserBubble(text = text)
                    } else {
                        AiBubble(text = text)
                    }
                }

                // Loading dots
                if (viewModel.isLoading) {
                    item { LoadingDots() }
                }

                // Song results
                if (viewModel.songs.isNotEmpty()) {
                    item {
                        Text(
                            "Here's what I found for you",
                            color      = TextSub,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier   = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )
                    }
                    items(viewModel.songs) { song ->
                        AISongCard(
                            name       = song.name,
                            artist     = song.artist ?: "",
                            imageUrl   = song.image ?: "",
                            onClick    = {
                                PlayerManager.currentSong = song
                                navController.navigate("player")
                            }
                        )
                    }
                }

                // Bottom spacer so last item clears input bar
                item { Spacer(Modifier.height(8.dp)) }
            }
        }

        // ── Input bar — floats above bottom nav ───────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, BgPage.copy(0.97f), BgPage),
                        startY = 0f,
                        endY   = 80f
                    )
                )
                .padding(horizontal = 20.dp)
                .padding(bottom = 104.dp, top = 16.dp)  // 104 = nav bar height + gap
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(8.dp, RoundedCornerShape(50), ambientColor = Color(0x14000000))
                    .clip(RoundedCornerShape(50))
                    .background(BgCard)
                    .padding(start = 20.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value         = input,
                    onValueChange = { input = it },
                    modifier      = Modifier.weight(1f),
                    textStyle     = LocalTextStyle.current.copy(
                        color    = TextHero,
                        fontSize = 14.sp
                    ),
                    decorationBox = { inner ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (input.isEmpty()) {
                                Text("Tell me about your mood...", color = TextSub, fontSize = 14.sp)
                            }
                            inner()
                        }
                    }
                )
                Spacer(Modifier.width(8.dp))
                // Send button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (input.isNotBlank() && !viewModel.isLoading) Primary
                            else Primary.copy(0.35f)
                        )
                        .clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication        = null
                        ) {
                            if (input.isNotBlank() && !viewModel.isLoading) {
                                viewModel.sendMessage(input)
                                input = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            color       = Color.White,
                            strokeWidth = 2.dp,
                            modifier    = Modifier.size(16.dp)
                        )
                    } else {
                        Icon(
                            Icons.Outlined.AutoAwesome, "Send",
                            tint     = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Chat bubbles
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UserBubble(text: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart    = 20.dp,
                        topEnd      = 6.dp,
                        bottomStart = 20.dp,
                        bottomEnd   = 20.dp
                    )
                )
                .background(UserBubble)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(text, color = Color.White, fontSize = 14.sp, lineHeight = 21.sp)
        }
    }
}

@Composable
private fun AiBubble(text: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment     = Alignment.Bottom
    ) {
        // AI avatar dot
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFFEDE7F6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.AutoAwesome, null, tint = Primary, modifier = Modifier.size(13.dp))
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .shadow(3.dp, RoundedCornerShape(topStart = 6.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp), ambientColor = Color(0x0A000000))
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(AiBubble)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(text, color = TextBody, fontSize = 14.sp, lineHeight = 21.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Animated loading dots
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LoadingDots() {
    val inf = rememberInfiniteTransition(label = "dots")
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment     = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFFEDE7F6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.AutoAwesome, null, tint = Primary, modifier = Modifier.size(13.dp))
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .shadow(3.dp, RoundedCornerShape(topStart = 6.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp), ambientColor = Color(0x0A000000))
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(AiBubble)
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf(0, 150, 300).forEach { delayMs ->
                    val dotAlpha by inf.animateFloat(
                        initialValue  = 0.25f,
                        targetValue   = 1f,
                        animationSpec = infiniteRepeatable(
                            animation  = tween(500, delayMillis = delayMs, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot$delayMs"
                    )
                    val dotScale by inf.animateFloat(
                        initialValue  = 0.7f,
                        targetValue   = 1f,
                        animationSpec = infiniteRepeatable(
                            animation  = tween(500, delayMillis = delayMs, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dotScale$delayMs"
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .scale(dotScale)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = dotAlpha))
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AI Song card — compact, white, with play arrow
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AISongCard(
    name: String,
    artist: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp), ambientColor = Color(0x0A000000))
            .clip(RoundedCornerShape(18.dp))
            .background(BgCard)
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryL)
            ) {
                AsyncImage(
                    model              = imageUrl,
                    contentDescription = name,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    color      = TextHero,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(artist, color = TextSub, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(PrimaryL)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("MUSIC", color = Primary, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                }
            }
            Spacer(Modifier.width(8.dp))
            // Play button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryL),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, "Play", tint = Primary, modifier = Modifier.size(20.dp))
            }
        }
    }
}