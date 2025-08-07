package com.example.smartwaste_admin.presentation.screens.routesscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.smartwaste_admin.presentation.viewmodels.areaviewmodel.AreaViewModel
import com.example.smartwaste_admin.presentation.viewmodels.routesviewmodel.RoutesViewModel

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
    var newArea by remember { mutableStateOf("") }
    val addedAreas = remember { mutableStateListOf<String>() }

    LaunchedEffect(routeId) {
        viewModel.getRouteById(routeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Route Details", color = Color.Black) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                navigationIcon = {

                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    },

            )
        },
        containerColor = Color.White,
        modifier = modifier.fillMaxSize()
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            when {
                routeState.isLoading -> {
                    CircularProgressIndicator(color = Color.Blue)
                }

                routeState.error.isNotEmpty() -> {
                    Text(
                        text = "Error: ${routeState.error}",
                        color = Color.Red
                    )
                }

                routeState.success != null -> {
                    val route = routeState.success

                    Text("Route ID: ${route.id}", color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Name: ${route.name}", color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Status: ${if (route.isActive) "Active" else "Inactive"}", color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Areas Covered:", color = Color.Black)
                    val allAreas = route.areaList + addedAreas
                    allAreas.forEach { area ->
                        Text("- $area", color = Color.DarkGray)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Add New Area to Route", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newArea,
                        onValueChange = { newArea = it },
                        label = { Text("Area Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (newArea.isNotBlank() && !route.areaList.contains(newArea.trim())) {
                                val updatedAreaList = route.areaList + newArea.trim()
                                val updatedRoute = route.copy(areaList = updatedAreaList).copy(id = routeId)
                                viewModel.updateRoute(updatedRoute)

                                newArea = ""
                                addedAreas.clear()
                                addedAreas.addAll(updatedAreaList)
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Add Area")
                    }
                }
            }
        }
    }
}