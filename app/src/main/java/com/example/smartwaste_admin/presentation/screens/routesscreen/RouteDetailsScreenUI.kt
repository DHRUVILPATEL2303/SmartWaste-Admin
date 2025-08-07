package com.example.smartwaste_admin.presentation.screens.routesscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.smartwaste_admin.data.models.AreaModel
import com.example.smartwaste_admin.presentation.viewmodels.areaviewmodel.AreaViewModel
import com.example.smartwaste_admin.presentation.viewmodels.routesviewmodel.RoutesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScreenUI(
    routeId: String,
    viewModel: RoutesViewModel = hiltViewModel(),
    areaViewModel: AreaViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val routeState = viewModel.routeByIdState.collectAsState().value
    val allAreaState = areaViewModel.allAreaState.collectAsState().value
    val coroutineScope = rememberCoroutineScope()

    var selectedArea by remember { mutableStateOf<AreaModel?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(routeId) {
        viewModel.getRouteById(routeId)
        areaViewModel.getAllAreas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Route Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.semantics { contentDescription = "Go back" }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            when {
                routeState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                routeState.error.isNotEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Error: ${routeState.error}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                routeState.success != null -> {
                    val route = routeState.success

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Route ID: ${route.id}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Name: ${route.name}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Status: ${if (route.isActive) "Active" else "Inactive"}",
                                color = if (route.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    Text("Areas Covered:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    val allAreas = route.areaList.distinct()

                    if (allAreas.isEmpty()) {
                        Text("No areas assigned", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(allAreas.size) { index ->
                                val areaName = allAreas[index]
                                AreaTag(areaName = areaName)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Add New Area to Route", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (allAreaState.isLoading) {
                        Box(Modifier.fillMaxWidth(), Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (allAreaState.error.isNotEmpty()) {
                        Text("Failed to load areas: ${allAreaState.error}")
                    } else {
                        val availableAreas = allAreaState.success.orEmpty()
                            .filter { it.areaName !in allAreas }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedArea?.areaName ?: "",
                                onValueChange = {},
                                label = { Text("Select Area") },
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                if (availableAreas.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No areas available") },
                                        onClick = {},
                                        enabled = false
                                    )
                                } else {
                                    availableAreas.forEach { area ->
                                        DropdownMenuItem(
                                            text = { Text(area.areaName) },
                                            onClick = {
                                                selectedArea = area
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showConfirmDialog = true },
                            enabled = selectedArea != null,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Add Area")
                        }

                        if (showConfirmDialog && selectedArea != null) {
                            AlertDialog(
                                onDismissRequest = { showConfirmDialog = false },
                                title = { Text("Confirm Add Area") },
                                text = { Text("Add ${selectedArea?.areaName} to the route?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            selectedArea?.let { area ->
                                                if (area.areaName !in route.areaList) {
                                                    val updatedAreaList = route.areaList + area.areaName
                                                    val updatedRoute = route.copy(areaList = updatedAreaList, id = routeId)
                                                    viewModel.updateRoute(updatedRoute)
                                                }
                                                selectedArea = null
                                                showConfirmDialog = false
                                            }
                                        }
                                    ) { Text("Confirm") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showConfirmDialog = false }) {
                                        Text("Cancel")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AreaTag(
    areaName: String,

    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
          ,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp
    ) {
        Text(
            text = areaName,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}