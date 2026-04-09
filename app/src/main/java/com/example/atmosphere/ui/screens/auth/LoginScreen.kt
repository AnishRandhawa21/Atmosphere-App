package com.example.atmosphere.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import com.example.atmosphere.ui.navigation.Routes
import com.example.atmosphere.ui.theme.*
import com.example.atmosphere.utils.TokenManager

// ── Color tokens for new design ───────────────────────────────────────────────
private val AtmospherePrimary     = Color(0xFF9575CD)
private val AtmospherePrimaryDark = Color(0xFF7B52C4)
private val AtmosphereBackground  = Color(0xFFEDEAF5)
private val AtmosphereBackground2 = Color(0xFFE4DFF0)
private val AtmosphereCard        = Color.White
private val AtmosphereInputBg     = Color(0xFFF3F0FA)
private val AtmosphereOnSurface   = Color(0xFF1A1A2E)
private val AtmosphereMuted       = Color(0xFF8888A0)
private val AtmosphereBorder      = Color(0xFFD8D0F0)

@Composable
fun LoginScreen(navController: NavController) {

    // ── Dependencies ──────────────────────────────────────────────────────────
    val context      = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val repository   = remember { AuthRepository(RetrofitClient.getClient(context), tokenManager) }
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(repository))

    // ── Local UI state ────────────────────────────────────────────────────────
    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
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
            ),
        contentAlignment = Alignment.Center
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600)) + slideInVertically(
                initialOffsetY = { it / 8 },
                animationSpec  = tween(600, easing = EaseOutCubic)
            )
        ) {
            Column(
                modifier             = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalAlignment  = Alignment.CenterHorizontally
            ) {

                // ── Dot-grid logo ─────────────────────────────────────────────
                AtmosphereLogo()

                Spacer(modifier = Modifier.height(10.dp))

                // ── App name ──────────────────────────────────────────────────
                Text(
                    text  = "Atmosphere",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color        = AtmospherePrimary,
                        fontWeight   = FontWeight.SemiBold,
                        fontSize     = 22.sp,
                        letterSpacing = 0.3.sp
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ── Card ──────────────────────────────────────────────────────
                Surface(
                    modifier       = Modifier.fillMaxWidth(),
                    shape          = RoundedCornerShape(24.dp),
                    color          = AtmosphereCard,
                    shadowElevation = 8.dp,
                    tonalElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
                    ) {

                        // Headline
                        Text(
                            text  = "Welcome back",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color      = AtmosphereOnSurface,
                                fontSize   = 24.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text  = "Step back into your sanctuary of sound.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AtmosphereMuted
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // ── Email ─────────────────────────────────────────────
                        AtmosphereFieldLabel(text = "USERNAME")
                        Spacer(modifier = Modifier.height(6.dp))
                        AtmosphereTextField(
                            value         = email,
                            onValueChange = { email = it },
                            placeholder   = "Enter Username",
                            keyboardType  = KeyboardType.Email
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // ── Password row ──────────────────────────────────────
                        AtmosphereFieldLabel(text = "PASSWORD")
                        Spacer(modifier = Modifier.height(6.dp))
                        AtmosphereTextField(
                            value               = password,
                            onValueChange       = { password = it },
                            placeholder         = "Enter Password",
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

                        // ── Sign In button ────────────────────────────────────
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
                                onClick  = { viewModel.login(email, password) },
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
                                        text  = "Sign In  →",
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
                    visible = viewModel.loginState == "ERROR",
                    enter   = fadeIn() + expandVertically(),
                    exit    = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text      = "Login failed. Please check your credentials.",
                        style     = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.error
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // ── Sign up link ──────────────────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "Don't have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AtmosphereMuted
                    )
                    TextButton(
                        onClick        = { navController.navigate(Routes.Signup.route) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text  = "Sign Up",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color      = AtmospherePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }

        // ── SUCCESS nav ───────────────────────────────────────────────────────
        if (viewModel.loginState == "SUCCESS") {
            LaunchedEffect(true) {
                navController.navigate(Routes.Main.route) {
                    popUpTo(Routes.Login.route) { inclusive = true }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared sub-components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun AtmosphereLogo() {
    val primary = AtmospherePrimary
    val rows = listOf(
        listOf(0.20f, 0.40f, 1.00f, 0.40f, 0.20f),
        listOf(0.40f, 1.00f, 1.00f, 1.00f, 0.40f),
        listOf(1.00f, 1.00f, 1.00f, 1.00f, 1.00f),
        listOf(0.40f, 1.00f, 1.00f, 1.00f, 0.40f),
        listOf(0.20f, 0.40f, 1.00f, 0.40f, 0.20f),
    )
    Column(
        horizontalAlignment  = Alignment.CenterHorizontally,
        verticalArrangement  = Arrangement.spacedBy(3.dp)
    ) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                row.forEach { alpha ->
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(primary.copy(alpha = alpha))
                    )
                }
            }
        }
    }
}

@Composable
internal fun AtmosphereFieldLabel(text: String) {
    Text(
        text  = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight    = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
            fontSize      = 11.sp,
            color         = AtmosphereMuted
        )
    )
}

@Composable
internal fun AtmosphereTextField(
    value               : String,
    onValueChange       : (String) -> Unit,
    placeholder         : String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType        : KeyboardType         = KeyboardType.Text,
    trailingIcon        : (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value                = value,
        onValueChange        = onValueChange,
        placeholder          = {
            Text(
                text  = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = AtmosphereMuted.copy(alpha = 0.6f)
            )
        },
        visualTransformation = visualTransformation,
        keyboardOptions      = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon         = trailingIcon,
        singleLine           = true,
        modifier             = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape  = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor   = AtmosphereInputBg,
            unfocusedContainerColor = AtmosphereInputBg,
            disabledContainerColor  = AtmosphereInputBg,
            focusedBorderColor      = AtmospherePrimary.copy(alpha = 0.5f),
            unfocusedBorderColor    = Color.Transparent,
            cursorColor             = AtmospherePrimary,
            focusedTextColor        = AtmosphereOnSurface,
            unfocusedTextColor      = AtmosphereOnSurface
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = AtmosphereOnSurface)
    )
}