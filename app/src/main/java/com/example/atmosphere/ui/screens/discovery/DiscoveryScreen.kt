package com.example.atmosphere.ui.screens.discovery

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
fun DiscoveryScreen(
    navController: NavController,
    repo: MusicRepository
) {
    val viewModel: DiscoveryViewModel = viewModel(factory = DiscoveryViewModelFactory(repo))
    val songs        by viewModel.songs.collectAsState()
    val listState     = rememberLazyListState()
    val focusManager  = LocalFocusManager.current

    var query         by remember { mutableStateOf("") }
    var searchFocused by remember { mutableStateOf(false) }

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
                        "Discover\nNew Sound",
                        color      = TextHero,
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 40.sp,
                        fontFamily = Serif
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("Search by song, artist, or mood.", color = TextSub, fontSize = 13.sp)
                }
            }

            // ── Search bar ────────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 20.dp)
                ) {
                    val borderColor by animateColorAsState(
                        targetValue   = if (searchFocused) Primary else Color.Transparent,
                        animationSpec = tween(200),
                        label         = "border"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(50), ambientColor = Color(0x14000000))
                            .clip(RoundedCornerShape(50))
                            .background(BgCard)
                            .border(1.5.dp, borderColor, RoundedCornerShape(50))
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val iconTint by animateColorAsState(
                            targetValue   = if (searchFocused) Primary else TextSub,
                            animationSpec = tween(200),
                            label         = "icon"
                        )
                        Icon(Icons.Outlined.Search, null, tint = iconTint, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        BasicTextField(
                            value         = query,
                            onValueChange = { query = it; viewModel.searchSongs(it) },
                            singleLine      = true,
                            textStyle       = TextStyle(color = TextHero, fontSize = 14.sp),
                            cursorBrush     = SolidColor(Primary),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                            decorationBox   = { inner ->
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp)) {
                                    if (query.isEmpty()) Text("Search songs, artists, moods…", color = TextSub, fontSize = 14.sp)
                                    inner()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { searchFocused = it.isFocused }
                        )
                        AnimatedVisibility(visible = query.isNotEmpty(), enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF0F0F4))
                                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                        query = ""; viewModel.searchSongs(""); focusManager.clearFocus()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Close, null, tint = TextSub, modifier = Modifier.size(11.dp))
                            }
                        }
                    }
                }
            }

            // ── Loading ───────────────────────────────────────────────────────
            if (viewModel.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                    }
                }
            }

            // ── Error ─────────────────────────────────────────────────────────
            viewModel.errorMessage?.let { msg ->
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.ErrorOutline, null, tint = Primary.copy(0.6f), modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(msg, color = TextSub, fontSize = 13.sp)
                    }
                }
            }

            // ── Section label ─────────────────────────────────────────────────
            if (!viewModel.isLoading && songs.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            if (query.isNotBlank()) "Results" else "Recommended",
                            color = TextHero, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = Serif
                        )
                        Text("${songs.size} tracks", color = TextSub, fontSize = 12.sp)
                    }
                }
            }

            // ── Empty state ───────────────────────────────────────────────────
            if (!viewModel.isLoading && songs.isEmpty() && query.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(PrimaryL), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.SearchOff, null, tint = Primary.copy(0.6f), modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("No results found", color = TextBody, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("Try a different search", color = TextSub, fontSize = 13.sp)
                    }
                }
            }

            // ── Song cards — shared SongCard ──────────────────────────────────
            itemsIndexed(songs, key = { _, s -> s.url }) { index, song ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { kotlinx.coroutines.delay(index * 55L); visible = true }
                AnimatedVisibility(visible = visible, enter = fadeIn(tween(280)) + slideInVertically(tween(280)) { it / 3 }) {
                    SongCard(
                        song          = song,
                        navController = navController,
                        onLike        = { viewModel.toggleLike(song) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscIconBtn(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(BgCard)
            .shadow(2.dp, CircleShape, ambientColor = Color(0x0A000000))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}