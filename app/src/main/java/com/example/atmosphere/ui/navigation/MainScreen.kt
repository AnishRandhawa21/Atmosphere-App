package com.example.atmosphere.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.atmosphere.data.remote.RetrofitClient
import com.example.atmosphere.data.repository.MusicRepository
import com.example.atmosphere.ui.screens.ai.AIScreen
import com.example.atmosphere.ui.screens.discovery.DiscoveryScreen
import com.example.atmosphere.ui.screens.home.HomeScreen
import com.example.atmosphere.ui.screens.mood.MoodScreen
import com.example.atmosphere.ui.screens.player.PlayerScreen
import com.example.atmosphere.ui.theme.*

// ── Route order for directional slide transitions ──────────────────────────
private val routeOrder = listOf(
    Routes.Home.route,
    Routes.Mood.route,
    Routes.Discovery.route,
    Routes.AI.route,
    Routes.Player.route
)

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun MainScreen(rootNavController: NavHostController) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val api = remember { RetrofitClient.getClient(context) }
    val repo = remember { MusicRepository(api) }

    val items = listOf(
        BottomNavItem(Routes.Home.route,      Icons.Default.Home,        "Home"),
        BottomNavItem(Routes.Mood.route,      Icons.Default.Favorite,    "Mood"),
        BottomNavItem(Routes.Discovery.route, Icons.Default.Search,      "Discover"),
        BottomNavItem(Routes.AI.route,        Icons.Default.AutoAwesome, "AI"),
        BottomNavItem(Routes.Player.route,    Icons.Default.PlayArrow,   "Player")
    )

    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    // Track previous route so we know which direction to slide
    var previousRoute by remember { mutableStateOf(currentRoute) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0EFEA))
    ) {

        NavHost(
            navController    = navController,
            startDestination = Routes.Home.route,
            // ── Global enter/exit transitions ─────────────────────────────
            enterTransition  = {
                val fromIndex = routeOrder.indexOf(initialState.destination.route)
                val toIndex   = routeOrder.indexOf(targetState.destination.route)
                val slideDir  = if (toIndex >= fromIndex) 1 else -1

                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth * slideDir },
                    animationSpec  = tween(380, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(220))
            },
            exitTransition   = {
                val fromIndex = routeOrder.indexOf(initialState.destination.route)
                val toIndex   = routeOrder.indexOf(targetState.destination.route)
                val slideDir  = if (toIndex >= fromIndex) -1 else 1

                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth * slideDir },
                    animationSpec = tween(380, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(180))
            },
            popEnterTransition  = {
                slideInHorizontally(
                    initialOffsetX = { -it / 4 },
                    animationSpec  = tween(380, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(220))
            },
            popExitTransition   = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(380, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(180))
            }
        ) {
            composable(Routes.Home.route) {
                HomeScreen(
                    navController     = navController,
                    rootNavController = rootNavController,
                    repo              = repo
                )
            }
            composable(Routes.Mood.route)      { MoodScreen(navController, repo) }
            composable(Routes.Discovery.route) { DiscoveryScreen(navController, repo) }
            composable(Routes.AI.route)        { AIScreen(navController) }
            composable(Routes.Player.route)    { PlayerScreen() }
        }

        AtmosphereNavBar(
            items        = items,
            currentRoute = currentRoute,
            onItemClick  = { route ->
                navController.navigate(route) {
                    popUpTo(Routes.Home.route)
                    launchSingleTop = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Floating pill nav bar
// ─────────────────────────────────────────────────────────────────────────────

// Your design system colors
private val NavPrimary   = Color(0xFF9575CD)  // purple
private val NavSecondary = Color(0xFF81D4FA)  // sky
private val NavTertiary  = Color(0xFFB2DFDB)  // mint
private val NavSurface   = Color(0xFFEBEBE6)
private val NavBg        = Color(0xFFF0EFEA)

@Composable
private fun AtmosphereNavBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, NavBg.copy(alpha = 0.85f), NavBg)
                )
            )
            .padding(horizontal = 28.dp)
            .padding(bottom = 24.dp, top = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(
                    elevation    = 20.dp,
                    shape        = RoundedCornerShape(50),
                    ambientColor = NavPrimary.copy(alpha = 0.12f),
                    spotColor    = NavPrimary.copy(alpha = 0.18f)
                )
                .clip(RoundedCornerShape(50))
                .background(NavSurface),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    // Cycle accent colors: purple → sky → mint → purple …
                    val accentColor = when (index % 3) {
                        0    -> NavPrimary
                        1    -> Color(0xFF4DB6AC)   // deeper teal for better contrast
                        else -> Color(0xFF7986CB)   // indigo variant
                    }
                    AtmosphereNavItem(
                        item        = item,
                        selected    = currentRoute == item.route,
                        accentColor = accentColor,
                        onClick     = { onItemClick(item.route) }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Individual nav item
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AtmosphereNavItem(
    item: BottomNavItem,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val active = isPressed || isHovered

    // Pill background — gradient when selected
    val bgAlpha by animateFloatAsState(
        targetValue   = if (selected) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label         = "bg_alpha"
    )

    // Ripple on press
    val rippleScale by animateFloatAsState(
        targetValue   = if (active && !selected) 1f else 0f,
        animationSpec = tween(if (active) 260 else 160, easing = FastOutSlowInEasing),
        label         = "ripple"
    )

    // Icon tint
    val iconTint by animateColorAsState(
        targetValue = when {
            selected -> Color.White
            active   -> accentColor
            else     -> Color(0xFF9E9E9E)
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label         = "tint"
    )

    // Bounce scale on selection
    val itemScale by animateDpAsState(
        targetValue   = if (selected) 50.dp else 42.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    // Elevation glow when selected
    val elevation by animateDpAsState(
        targetValue   = if (selected) 8.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label         = "elevation"
    )

    Box(
        modifier = Modifier
            .size(itemScale)
            .shadow(
                elevation    = elevation,
                shape        = CircleShape,
                ambientColor = accentColor.copy(alpha = 0.3f),
                spotColor    = accentColor.copy(alpha = 0.4f)
            )
            .clip(CircleShape)
            .background(
                if (selected)
                    Brush.linearGradient(
                        colors = listOf(
                            accentColor,
                            accentColor.copy(alpha = 0.75f)
                        )
                    )
                else
                    Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .drawBehind {
                // Soft ripple ring on press
                if (!selected && rippleScale > 0f) {
                    drawCircle(
                        color  = accentColor.copy(alpha = 0.15f * rippleScale),
                        radius = (size.minDimension / 2f) * rippleScale,
                        center = Offset(size.width / 2f, size.height / 2f)
                    )
                }
            }
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector        = item.icon,
            contentDescription = item.label,
            tint               = iconTint,
            modifier           = Modifier.size(if (selected) 22.dp else 20.dp)
        )
    }
}