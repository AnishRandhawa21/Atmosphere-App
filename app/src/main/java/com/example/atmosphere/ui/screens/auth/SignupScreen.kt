package com.example.atmosphere.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.example.atmosphere.data.remote.RetrofitClient
import com.example.atmosphere.data.repository.AuthRepository
import com.example.atmosphere.ui.theme.*
import com.example.atmosphere.utils.TokenManager

// ── Color tokens (same ramp as LoginScreen) ───────────────────────────────────
private val AtmospherePrimary     = Color(0xFF9575CD)
private val AtmospherePrimaryDark = Color(0xFF7B52C4)
private val AtmosphereBackground  = Color(0xFFEDEAF5)
private val AtmosphereBackground2 = Color(0xFFE4DFF0)
private val AtmosphereCard        = Color.White
private val AtmosphereInputBg     = Color(0xFFF3F0FA)
private val AtmosphereOnSurface   = Color(0xFF1A1A2E)
private val AtmosphereMuted       = Color(0xFF8888A0)

@Composable
fun SignupScreen(navController: NavController) {

    // ── Dependencies ──────────────────────────────────────────────────────────
    val context      = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val repository   = remember { AuthRepository(RetrofitClient.getClient(context), tokenManager) }
    val viewModel: SignupViewModel = viewModel(factory = SignupViewModelFactory(repository))

    // ── Local UI state ────────────────────────────────────────────────────────
    var username        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // ── Entry animation ───────────────────────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // ── Root background ───────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(AtmosphereBackground, AtmosphereBackground2)
                )
            )
    ) {

        // ── Back button ───────────────────────────────────────────────────────
        IconButton(
            onClick  = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.85f))
        ) {
            Icon(
                imageVector        = Icons.Filled.ArrowBack,
                contentDescription = "Go back",
                tint               = AtmospherePrimary
            )
        }

        // ── Scrollable content ────────────────────────────────────────────────
        AnimatedVisibility(
            visible  = visible,
            enter    = fadeIn(tween(600)) + slideInVertically(
                initialOffsetY = { it / 8 },
                animationSpec  = tween(600, easing = EaseOutCubic)
            ),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── Dot-grid logo ─────────────────────────────────────────────
                AtmosphereLogo()

                Spacer(modifier = Modifier.height(10.dp))

                // ── App name ──────────────────────────────────────────────────
                Text(
                    text  = "Atmosphere",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color         = AtmospherePrimary,
                        fontWeight    = FontWeight.SemiBold,
                        fontSize      = 22.sp,
                        letterSpacing = 0.3.sp
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ── Card ──────────────────────────────────────────────────────
                Surface(
                    modifier        = Modifier.fillMaxWidth(),
                    shape           = RoundedCornerShape(24.dp),
                    color           = AtmosphereCard,
                    shadowElevation = 8.dp,
                    tonalElevation  = 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
                    ) {

                        // Headline
                        Text(
                            text  = "Create account",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color      = AtmosphereOnSurface,
                                fontSize   = 24.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text  = "Your sanctuary of sound awaits.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AtmosphereMuted
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // ── Username ──────────────────────────────────────────
                        AtmosphereFieldLabel(text = "USERNAME")
                        Spacer(modifier = Modifier.height(6.dp))
                        AtmosphereTextField(
                            value         = username,
                            onValueChange = { username = it },
                            placeholder   = "Choose a username"
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // ── Email ─────────────────────────────────────────────
//                        AtmosphereFieldLabel(text = "EMAIL ADDRESS")
//                        Spacer(modifier = Modifier.height(6.dp))
//                        AtmosphereTextField(
//                            value         = email,
//                            onValueChange = { email = it },
//                            placeholder   = "name@sanctuary.com",
//                            keyboardType  = KeyboardType.Email
//                        )
//
//                        Spacer(modifier = Modifier.height(18.dp))

                        // ── Password ──────────────────────────────────────────
                        AtmosphereFieldLabel(text = "PASSWORD")
                        Spacer(modifier = Modifier.height(6.dp))
                        AtmosphereTextField(
                            value               = password,
                            onValueChange       = { password = it },
                            placeholder         = "Create a password",
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            keyboardType = KeyboardType.Password,
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector        = if (passwordVisible)
                                            Icons.Filled.Visibility
                                        else
                                            Icons.Filled.VisibilityOff,
                                        contentDescription = "Toggle password",
                                        tint               = AtmosphereMuted
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(26.dp))

                        // ── Create Account button ─────────────────────────────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(AtmospherePrimary, AtmospherePrimaryDark)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick  = { viewModel.signup(username, password) },
                                modifier = Modifier.fillMaxSize(),
                                shape    = RoundedCornerShape(25.dp),
                                colors   = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor   = Color.White
                                ),
                                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
                            ) {
                                if (viewModel.isLoading) {
                                    CircularProgressIndicator(
                                        color       = Color.White,
                                        modifier    = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text  = "Create Account  →",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight    = FontWeight.SemiBold,
                                            letterSpacing = 0.3.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Error banner ──────────────────────────────────────────────
                AnimatedVisibility(
                    visible = viewModel.signupState == "ERROR",
                    enter   = fadeIn() + expandVertically(),
                    exit    = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text      = "Signup failed. Please try again.",
                        style     = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.error
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Back to login link ────────────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "Already have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AtmosphereMuted
                    )
                    TextButton(
                        onClick        = { navController.popBackStack() },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text  = "Sign In",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color      = AtmospherePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }

        // ── SUCCESS: pop back to login ────────────────────────────────────────
        if (viewModel.signupState == "SUCCESS") {
            LaunchedEffect(Unit) {
                navController.popBackStack()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Private sub-components (mirrors LoginScreen shared components)
// ─────────────────────────────────────────────────────────────────────────────
