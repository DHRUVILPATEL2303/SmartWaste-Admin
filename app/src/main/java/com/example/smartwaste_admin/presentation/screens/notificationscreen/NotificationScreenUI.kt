package com.example.smartwaste_admin.presentation.screens.notificationscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartwaste_admin.presentation.viewmodels.notificationviewmodel.NotificationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreenUI(
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val state by viewModel.notificationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val primaryColor = Color(0xFF00796B)
    val lightGrayColor = Color(0xFFF0F0F0)
    val darkTextColor = Color(0xFF1C1C1E)
    val mediumTextColor = Color(0xFF8A8A8E)

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = Color.LightGray,
        cursorColor = primaryColor,
        focusedLabelColor = primaryColor,
        unfocusedLabelColor = mediumTextColor,
        focusedLeadingIconColor = primaryColor,
        unfocusedLeadingIconColor = mediumTextColor,
        focusedTextColor = darkTextColor,
        unfocusedTextColor = darkTextColor
    )

    LaunchedEffect(state.success, state.error) {
        state.success?.let { successMessage ->
            snackbarHostState.showSnackbar(successMessage)
            viewModel.resetState()
            title = ""
            message = ""
        }
        if (state.error.isNotBlank()) {
            snackbarHostState.showSnackbar(state.error)
            viewModel.resetState()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Broadcast Message",
                        color = darkTextColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.White
                )
            )
        },

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                Icon(
                    imageVector = Icons.Filled.Forum,
                    contentDescription = "Broadcast Icon",
                    tint = lightGrayColor,
                    modifier = Modifier.size(80.dp)
                )

                Text(
                    text = "Compose and send a notification to all registered users.",
                    color = mediumTextColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Notification Title") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Title, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    label = { Text("Notification Message") },
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (title.isBlank() || message.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Title and message cannot be empty.")
                            }
                        } else {
                            viewModel.sendNotificationToAllUsers(title.trim(), message.trim())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White,
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Send to All Users", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryColor)
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        shape = RoundedCornerShape(12.dp),
                        containerColor = darkTextColor,
                        contentColor = Color.White,
                        actionColor = primaryColor,
                        dismissActionContentColor = Color.White
                    )
                }
            )
        }
    }
}