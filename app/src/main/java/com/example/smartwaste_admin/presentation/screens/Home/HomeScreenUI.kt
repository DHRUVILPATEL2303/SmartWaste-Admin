package com.example.smartwaste_admin.presentation.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.smartwaste_admin.data.models.RouteModel
import com.example.smartwaste_admin.data.models.TruckModel
import com.example.smartwaste_admin.presentation.viewmodels.routesviewmodel.RoutesViewModel
import com.example.smartwaste_admin.presentation.viewmodels.truckViewModel.TruckViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenUI(
    modifier: Modifier = Modifier,
    truckViewModel: TruckViewModel = hiltViewModel(),
    routesViewModel: RoutesViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val truckState by truckViewModel.allTrucksState.collectAsState()
    val routeState by routesViewModel.allRoutesState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        truckViewModel.getAllTrucks()
        routesViewModel.getAllRoutes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Admin Dashboard",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("add_area") },
                    containerColor = Color.White,
                    contentColor = Color(0xFF0288D1),
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Add Area")
                }
                FloatingActionButton(
                    onClick = { navController.navigate("add_route") },
                    containerColor = Color.White,
                    contentColor = Color(0xFF0288D1),
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.Default.Map, contentDescription = "Add Route")
                }
                FloatingActionButton(
                    onClick = { navController.navigate("add_truck") },
                    containerColor = Color.White,
                    contentColor = Color(0xFF0288D1),
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = "Add Truck")
                }
            }
        },
        containerColor = Color.White,
        contentColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)),
                label = { Text("Search Truck by Number", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF0288D1)
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0288D1),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    cursorColor = Color(0xFF0288D1),

                )
            )


            Text(
                "All Trucks",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            when {
                truckState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp),
                        color = Color(0xFF0288D1)
                    )
                }
                truckState.error.isNotEmpty() -> {
                    Text(
                        "Error: ${truckState.error}",
                        color = Color(0xFFB00020),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                else -> {
                    val filteredTrucks = truckState.success?.filter {
                        it.truckNumber.contains(searchQuery, ignoreCase = true)
                    } ?: emptyList()

                    if (filteredTrucks.isEmpty()) {
                        Text(
                            "No trucks found",
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    } else {
                        LazyColumn {
                            items(filteredTrucks) { truck ->
                                TruckItem(
                                    truck = truck,
                                    onClick = { navController.navigate("truck_details/${truck.id}") }
                                )
                            }
                        }
                    }
                }
            }

            // Routes Section
            Text(
                "All Routes",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            when {
                routeState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp),
                        color = Color(0xFF0288D1)
                    )
                }
                routeState.error.isNotEmpty() -> {
                    Text(
                        "Error: ${routeState.error}",
                        color = Color(0xFFB00020),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                else -> {
                    val routes = routeState.success ?: emptyList()
                    if (routes.isEmpty()) {
                        Text(
                            "No routes found",
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    } else {
                        LazyColumn {
                            items(routes) { route ->
                                RouteItem(
                                    route = route,
                                    onClick = { navController.navigate("route_details/${route.id}") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TruckItem(truck: TruckModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFF5F5F5)
                    )
                )
            )
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocalShipping,
                contentDescription = null,
                tint = Color(0xFF0288D1),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Truck #: ${truck.truckNumber}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                )
                Text(
                    "Route ID: ${truck.routeId}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
                Text(
                    "Status: ${truck.status}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (truck.status == "Active") Color(0xFF4CAF50) else Color(0xFFB00020)
                    )
                )
            }
        }
    }
}

@Composable
fun RouteItem(route: RouteModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFF5F5F5)
                    )
                )
            )
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Map,
                contentDescription = null,
                tint = Color(0xFF0288D1),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Route: ${route.name}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                )
                Text(
                    "Areas: ${route.areaList.joinToString()}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
                Text(
                    "Active: ${if (route.isActive) "Yes" else "No"}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (route.isActive) Color(0xFF4CAF50) else Color(0xFFB00020)
                    )
                )
            }
        }
    }
}