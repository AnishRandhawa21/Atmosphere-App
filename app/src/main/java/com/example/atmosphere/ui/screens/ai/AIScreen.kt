package com.example.atmosphere.ui.screens.ai

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.atmosphere.data.remote.RetrofitClient
import com.example.atmosphere.data.repository.MusicRepository
import com.example.atmosphere.utils.PlayerManager
import coil.compose.AsyncImage
private val BgTop         = Color(0xFFEEECF8)
private val BgBottom      = Color(0xFFF5F4FC)
private val Primary       = Color(0xFF9575CD)
private val UserBubble    = Color(0xFFB39DDB)
private val TextPrimary   = Color(0xFF2D2340)
private val TextSecondary = Color(0xFF8E84A3)

@Composable
fun AIScreen(navController: NavController) {

    val context   = LocalContext.current
    val api       = remember { RetrofitClient.getClient(context) }
    val repo      = remember { MusicRepository(api) }
    val viewModel = remember { AIViewModel(repo) }

    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty())
            listState.animateScrollToItem(viewModel.messages.size - 1)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(BgTop, BgBottom)))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
        ) {

            Text(
                text = "AI Music Assistant",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tell me how you feel 🎧",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 👉 MOVE YOUR LazyColumn HERE


            // ── Chat messages ─────────────────────────────────────────────────
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 90.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(viewModel.messages) { msg ->
                    // Simple heuristic: messages starting with "You:" are user messages
                    val isUser = msg.startsWith("You:")
                    val text = msg.removePrefix("You:").removePrefix("AI:").trim()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = if (isUser) 20.dp else 4.dp,
                                        topEnd = if (isUser) 4.dp else 20.dp,
                                        bottomStart = 20.dp,
                                        bottomEnd = 20.dp
                                    )
                                )
                                .background(if (isUser) UserBubble else Color.White)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = text,
                                color = if (isUser) Color.White else TextPrimary,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                // ── Song results ──────────────────────────────────────────────
                if (viewModel.songs.isNotEmpty()) {
                    items(viewModel.songs) { song ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                                .clickable {
                                    PlayerManager.currentSong = song
                                    navController.navigate("player")
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = song.image,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.name,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = song.artist ?: "",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Color(0xFFF0EBF8))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "• MUSIC",
                                        color = Primary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Input bar ─────────────────────────────────────────────────────
        val bottomPadding = WindowInsets.navigationBars
            .asPaddingValues()
            .calculateBottomPadding()

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BgBottom.copy(alpha = 0.95f),
                            BgBottom
                        )
                    )
                )
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = bottomPadding + 90.dp
                )
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value         = input,
                    onValueChange = { input = it },
                    modifier      = Modifier.weight(1f),
                    textStyle     = LocalTextStyle.current.copy(
                        color    = TextPrimary,
                        fontSize = 15.sp
                    ),
                    decorationBox = { inner ->
                        if (input.isEmpty()) {
                            Text("Tell me about your mood...", color = TextSecondary, fontSize = 15.sp)
                        }
                        inner()
                    }
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(listOf(Color(0xFFAA8FE0), Primary))
                        )
                        .clickable {
                            if (input.isNotBlank()) {
                                viewModel.sendMessage(input)
                                input = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.AutoAwesome,
                        contentDescription = "Send",
                        tint               = Color.White,
                        modifier           = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}