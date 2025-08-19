package com.example.smartwaste_admin

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.smartwaste_admin.presentation.navigation.AppNavigation
import com.example.smartwaste_admin.ui.theme.SmartWasteAdminTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

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
        }
    }
}

@Composable
fun AppSecurityContainer() {
    var isUnlocked by remember { mutableStateOf(false) }
    var isAppInBackground by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val activity = context as FragmentActivity

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                isAppInBackground = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val biometricPrompt = remember {
        BiometricPrompt(activity, ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isUnlocked = true
                    isAppInBackground = false
                }
            })
    }

    val canUseBiometrics = remember {
        BiometricManager.from(context)
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {
            AppNavigation()

            val shouldShowLockScreen = !isUnlocked || isAppInBackground
            AnimatedVisibility(
                visible = shouldShowLockScreen,
                enter = fadeIn(animationSpec = spring()),
                exit = fadeOut(animationSpec = spring())
            ) {
                LockScreen(
                    canUseBiometrics = canUseBiometrics,
                    onBiometricAuth = {
                        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Unlock SmartWaste Admin")
                            .setSubtitle("Authenticate to continue")
                            .setNegativeButtonText("Cancel")
                            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                            .build()
                        biometricPrompt.authenticate(promptInfo)
                    },
                    onDeviceCredentialAuth = {
                        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Unlock SmartWaste Admin")
                            .setSubtitle("Enter your device passcode")
                            .setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                            .build()
                        biometricPrompt.authenticate(promptInfo)
                    }
                )
            }
        }
    }
}

@Composable
fun LockScreen(
    canUseBiometrics: Boolean,
    onBiometricAuth: () -> Unit,
    onDeviceCredentialAuth: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D47A1),
                        Color(0xFF002B4D)
                    )
                )
            )
            .padding(horizontal = 32.dp, vertical = 64.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Recycling,
                    contentDescription = "App Logo",
                    tint = Color.White,
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "SmartWaste Admin",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Secure access required",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (canUseBiometrics) {
                AuthButton(
                    icon = Icons.Default.Fingerprint,
                    title = "Unlock with Biometrics",
                    onClick = onBiometricAuth,
                    isPrimary = true
                )
            }

            AuthButton(
                icon = Icons.Default.Lock,
                title = "Use Device Passcode",
                onClick = onDeviceCredentialAuth,
                isPrimary = !canUseBiometrics
            )
        }
    }
}

@Composable
fun AuthButton(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isPrimary: Boolean
) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF0D47A1)
            )
        ) {
            AuthButtonContent(icon = icon, title = title)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
        ) {
            AuthButtonContent(icon = icon, title = title)
        }
    }
}

@Composable
private fun AuthButtonContent(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}