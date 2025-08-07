package com.example.smartwaste_admin.presentation.screens.routesscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.smartwaste_admin.data.models.RouteModel
import com.example.smartwaste_admin.presentation.viewmodels.areaviewmodel.AreaViewModel
import com.example.smartwaste_admin.presentation.viewmodels.routesviewmodel.RoutesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRouteScreenUI(
    routesViewModel: RoutesViewModel = hiltViewModel(),
    areaViewModel: AreaViewModel = hiltViewModel(),
    navController: NavHostController
) {
    var routeName by remember { mutableStateOf("") }
    val selectedAreas = remember { mutableStateListOf<String>() }

    val addRouteState = routesViewModel.addRouteState.collectAsState().value
    val areaState = areaViewModel.allAreaState.collectAsState().value


    LaunchedEffect(Unit) {
        areaViewModel.getAllAreas()
    }

    val white = Color.White
    val black = Color.Black
    val primary = Color(0xFF0D47A1)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Route", color = black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = white
                )
            )
        },
        containerColor = white,
        modifier = Modifier.fillMaxSize()
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = routeName,
                onValueChange = { routeName = it },
                label = { Text("Route Name", color = black) },
                textStyle = TextStyle(color = black),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(white),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = black,
                    unfocusedTextColor = black,
                    focusedLabelColor = black,
                    unfocusedLabelColor = black,
                    cursorColor = black,
                    focusedBorderColor = primary,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select Areas:",
                style = MaterialTheme.typography.titleMedium.copy(color = black)
            )
            Spacer(modifier = Modifier.height(8.dp))

            when {
                areaState.isLoading -> CircularProgressIndicator(color = primary)

                areaState.error.isNotEmpty() -> Text(
                    text = "Error: ${areaState.error}",
                    color = Color.Red
                )

                areaState.success != null -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(areaState.success) { area ->
                            val isSelected = selectedAreas.contains(area.areaName)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isSelected) selectedAreas.remove(area.areaName)
                                        else selectedAreas.add(area.areaName)
                                    }
                                    .padding(8.dp)
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = {
                                        if (it) selectedAreas.add(area.areaName)
                                        else selectedAreas.remove(area.areaName)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = primary,
                                        uncheckedColor = Color.Gray
                                    )
                                )
                                Text(text = area.areaName, color = black)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (routeName.isNotBlank() && selectedAreas.isNotEmpty()) {
                        val newRoute = RouteModel(
                            name = routeName.trim(),
                            areaList = selectedAreas.toList()
                        )
                        routesViewModel.addRoute(newRoute)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = Color.White
                )
            ) {
                Text("Add Route", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                addRouteState.isLoading -> CircularProgressIndicator(color = primary)

                addRouteState.success != null -> Text(
                    text = "Success: ${addRouteState.success}",
                    color = primary
                )

                addRouteState.error.isNotEmpty() -> Text(
                    text = "Error: ${addRouteState.error}",
                    color = Color.Red
                )
            }
        }
    }
}