package com.example.smartwaste_admin.presentation.screens.routesscreen

import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.smartwaste_admin.data.models.AreaInfo
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
    val selectedAreas = remember { mutableStateListOf<AreaInfo>() }

    val addRouteState = routesViewModel.addRouteState.collectAsState().value
    val areaState = areaViewModel.allAreaState.collectAsState().value

    LaunchedEffect(Unit) {
        areaViewModel.getAllAreas()
    }

    // Debug logging for area data
    LaunchedEffect(areaState.success) {
        areaState.success?.forEach { area ->
            Log.d("AddRouteScreenUI", "Area: ${area.areaName}")
            Log.d("AddRouteScreenUI", "Area ID: ${area.areadId}")
            Log.d("AddRouteScreenUI", "Latitude: ${area.latitude}")
            Log.d("AddRouteScreenUI", "Longitude: ${area.longitude}")
            Log.d("AddRouteScreenUI", "Latitude type: ${area.latitude::class.java.simpleName}")
            Log.d("AddRouteScreenUI", "Longitude type: ${area.longitude::class.java.simpleName}")
            Log.d("AddRouteScreenUI", "---")
        }
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

            // Show selected areas count
            if (selectedAreas.isNotEmpty()) {
                Text(
                    text = "${selectedAreas.size} area(s) selected",
                    style = MaterialTheme.typography.bodySmall.copy(color = primary),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                areaState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = primary)
                    }
                }

                areaState.error.isNotEmpty() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = "Error: ${areaState.error}",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                areaState.success != null -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(areaState.success) { area ->
                            val isSelected = selectedAreas.any { it.areaId == area.areadId }

                            // Safe coordinate conversion with fallback values
                            val safeLatitude = when {
                                area.latitude is Double -> area.latitude
                                area.latitude is String -> area.latitude.toDoubleOrNull() ?: 0.0
                                area.latitude is Float -> area.latitude.toDouble()
                                area.latitude is Int -> area.latitude.toDouble()
                                else -> {
                                    Log.w("AddRouteScreenUI", "Unknown latitude type for ${area.areaName}: ${area.latitude}")
                                    0.0
                                }
                            }

                            val safeLongitude = when {
                                area.longitude is Double -> area.longitude
                                area.longitude is String -> area.longitude.toDoubleOrNull() ?: 0.0
                                area.longitude is Float -> area.longitude.toDouble()
                                area.longitude is Int -> area.longitude.toDouble()
                                else -> {
                                    Log.w("AddRouteScreenUI", "Unknown longitude type for ${area.areaName}: ${area.longitude}")
                                    0.0
                                }
                            }

                            Log.d("AddRouteScreenUI", "Safe coordinates for ${area.areaName}: $safeLatitude, $safeLongitude")

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable {
                                        if (isSelected) {
                                            selectedAreas.removeAll { it.areaId == area.areadId }
                                        } else {
                                            val newAreaInfo = AreaInfo(
                                                areaId = area.areadId,
                                                areaName = area.areaName,
                                                latitude = safeLatitude,
                                                longitude = safeLongitude
                                            )
                                            selectedAreas.add(newAreaInfo)
                                            Log.d("AddRouteScreenUI", "Added area: ${newAreaInfo.areaName} with coordinates: ${newAreaInfo.latitude}, ${newAreaInfo.longitude}")
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) primary.copy(alpha = 0.1f) else Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                val newAreaInfo = AreaInfo(
                                                    areaId = area.areadId,
                                                    areaName = area.areaName,
                                                    latitude = safeLatitude,
                                                    longitude = safeLongitude
                                                )
                                                selectedAreas.add(newAreaInfo)
                                                Log.d("AddRouteScreenUI", "Checkbox added area: ${newAreaInfo.areaName} with coordinates: ${newAreaInfo.latitude}, ${newAreaInfo.longitude}")
                                            } else {
                                                selectedAreas.removeAll { it.areaId == area.areadId }
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = primary,
                                            uncheckedColor = Color.Gray
                                        )
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = area.areaName,
                                            color = black,
                                            fontWeight = FontWeight.Medium
                                        )

                                        // Show coordinates with warning if they're 0
                                        val coordinateText = "Lat: $safeLatitude, Lng: $safeLongitude"
                                        val coordinateColor = if (safeLatitude == 0.0 && safeLongitude == 0.0) {
                                            Color.Red
                                        } else {
                                            Color.Gray
                                        }

                                        Text(
                                            text = coordinateText,
                                            color = coordinateColor,
                                            style = MaterialTheme.typography.bodySmall
                                        )

                                        // Show warning for zero coordinates
                                        if (safeLatitude == 0.0 && safeLongitude == 0.0) {
                                            Text(
                                                text = "⚠️ No coordinates available",
                                                color = Color.Red,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show selected areas with coordinates
            if (selectedAreas.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = primary.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            "Selected Areas:",
                            fontWeight = FontWeight.Bold,
                            color = primary
                        )
                        selectedAreas.forEachIndexed { index, area ->
                            val coordinateWarning = if (area.latitude == 0.0 && area.longitude == 0.0) " ⚠️" else ""
                            Text(
                                "${index + 1}. ${area.areaName} (${area.latitude}, ${area.longitude})$coordinateWarning",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (area.latitude == 0.0 && area.longitude == 0.0) Color.Red else black,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (routeName.isNotBlank() && selectedAreas.isNotEmpty()) {
                        val newRoute = RouteModel(
                            name = routeName.trim(),
                            areaList = selectedAreas.map { selectedArea ->
                                Log.d("AddRouteScreenUI", "Creating route with area: ${selectedArea.areaName}, coordinates: ${selectedArea.latitude}, ${selectedArea.longitude}")
                                AreaInfo(
                                    areaId = selectedArea.areaId,
                                    areaName = selectedArea.areaName,
                                    latitude = selectedArea.latitude,
                                    longitude = selectedArea.longitude
                                )
                            }
                        )

                        Log.d("AddRouteScreenUI", "Final route object: $newRoute")
                        // Call ViewModel function to save route
                        routesViewModel.addRoute(newRoute)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = routeName.isNotBlank() && selectedAreas.isNotEmpty() && !addRouteState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = Color.White
                )
            ) {
                if (addRouteState.isLoading) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adding Route...", fontSize = 16.sp)
                    }
                } else {
                    Text("Add Route", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                addRouteState.success != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = "Success: ${addRouteState.success}",
                            color = Color.Green.copy(alpha = 0.8f),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                addRouteState.error.isNotEmpty() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = "Error: ${addRouteState.error}",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}