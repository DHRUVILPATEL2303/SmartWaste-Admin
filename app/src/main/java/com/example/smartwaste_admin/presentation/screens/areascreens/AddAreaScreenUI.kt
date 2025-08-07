package com.example.smartwaste_admin.presentation.screens.areascreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.smartwaste_admin.data.models.AreaModel
import com.example.smartwaste_admin.presentation.viewmodels.areaviewmodel.AreaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAreaScreenUI(
    viewModel: AreaViewModel = hiltViewModel(),
    navController: NavHostController
) {
    var areaName by remember { mutableStateOf("") }
    val addAreaState = viewModel.addAreaState.collectAsState().value


    val whiteBackground = Color.White
    val blackText = Color.Black
    val errorColor = Color.Red
    val primaryColor = Color(0xFF0D47A1)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Add New Area", color = blackText)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = blackText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = whiteBackground
                )
            )
        },
        containerColor = whiteBackground,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = areaName,
                onValueChange = { areaName = it },
                label = {
                    Text("Area Name", color = blackText)
                },
                textStyle = TextStyle(color = blackText),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(whiteBackground),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = blackText,
                    unfocusedTextColor = blackText,
                    focusedLabelColor = blackText,
                    unfocusedLabelColor = blackText,
                    cursorColor = blackText,
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color.Gray,
                    disabledTextColor = blackText
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (areaName.isNotBlank()) {
                        viewModel.addArea(AreaModel(areaName = areaName.trim()))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = Color.White
                )
            ) {
                Text("Add Area", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                addAreaState.isLoading -> {
                    CircularProgressIndicator(color = primaryColor)
                }

                addAreaState.error.isNotEmpty() -> {
                    Text(
                        text = "Error: ${addAreaState.error}",
                        color = errorColor,
                        fontSize = 14.sp
                    )
                }

                addAreaState.success != null -> {
                    Text(
                        text = "Success: ${addAreaState.success}",
                        color = primaryColor,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}