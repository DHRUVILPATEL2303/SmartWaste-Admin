package com.example.smartwaste_admin

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.smartwaste_admin.presentation.navigation.AppNavigation
import com.example.smartwaste_admin.ui.theme.SmartWasteAdminTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setRecentsScreenshotEnabled(false)
        }

        enableEdgeToEdge()

        setContent {
            SmartWasteAdminTheme {
                AppSecurityContainer()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}

@Composable
fun AppSecurityContainer() {
    var isLocked by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE,
                Lifecycle.Event.ON_STOP -> isLocked = true
                Lifecycle.Event.ON_RESUME -> isLocked = false
                else -> { /* no-op */ }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().blur(if (isLocked) 10.dp else 0.dp)) {
            AppNavigation()
        }

        AnimatedVisibility(visible = isLocked) {
            EnhancedLockScreenOverlay(appState = if (isLocked) AppState.LOCKED else AppState.ACTIVE)
        }
    }
}



@Composable
private fun EnhancedLockScreenOverlay(appState: AppState) {
    val infiniteTransition = rememberInfiniteTransition(label = "lock_animation")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .wrapContentSize(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E2E).copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(50),
                    color = Color(0xFF4A90E2).copy(alpha = glowAlpha)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .size(40.dp)
                                .graphicsLayer {
                                    scaleX = pulseScale
                                    scaleY = pulseScale
                                }
                        )
                    }
                }

                Text(
                    text = "Smart Waste Admin",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = when (appState) {
                        AppState.BACKGROUND -> "App Secured"
                        AppState.LOCKED -> "Session Locked"
                        AppState.ACTIVE -> ""
                    },
                    color = Color(0xFF9CA3AF),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = RoundedCornerShape(50),
                        color = Color(0xFF10B981)
                    ) {}

                    Text(
                        text = "Protected Mode Active",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Background pattern
        BackgroundPattern(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun BackgroundPattern(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pattern")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pattern_offset"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = offsetY
                alpha = 0.1f
            }
    ) {
        repeat(20) { index ->
            Surface(
                modifier = Modifier
                    .offset(
                        x = (index * 50).dp,
                        y = (index * 30).dp
                    )
                    .size(2.dp),
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.2f)
            ) {}
        }
    }
}

private enum class AppState {
    ACTIVE,
    BACKGROUND,
    LOCKED
}