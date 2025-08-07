package com.example.smartwaste_admin.presentation.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.smartwaste_admin.data.models.RouteModel
import com.example.smartwaste_admin.data.models.TruckModel
import com.example.smartwaste_admin.presentation.navigation.Routes
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
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                ),
                modifier = Modifier.shadow(2.dp)
            )
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    icon = Icons.Default.LocationOn,
                    text = "Add Area",
                    onClick = { navController.navigate(Routes.AddAreaScreen) }
                )
                ActionButton(
                    icon = Icons.Default.Map,
                    text = "Add Route",
                    onClick = { navController.navigate(Routes.AddRouteScreen) }
                )
                ActionButton(
                    icon = Icons.Default.LocalShipping,
                    text = "Add Truck",
                    onClick = { navController.navigate(Routes.AddTruckScreen) }
                )
            }


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF8F8F8)),
                        label = { Text("Search Truck by Number", color = Color(0xFF666666)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF0066CC)
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0066CC),
                            unfocusedBorderColor = Color(0xFFD0D0D0),
                            cursorColor = Color(0xFF0066CC),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }


                item {
                    Text(
                        "All Trucks",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                when {
                    truckState.isLoading -> {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally),
                                color = Color(0xFF0066CC)
                            )
                        }
                    }
                    truckState.error.isNotEmpty() -> {
                        item {
                            Text(
                                "Error: ${truckState.error}",
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    else -> {
                        val filteredTrucks = truckState.success?.filter {
                            it.truckNumber.contains(searchQuery, ignoreCase = true)
                        } ?: emptyList()

                        if (filteredTrucks.isEmpty()) {
                            item {
                                Text(
                                    "No trucks found",
                                    color = Color(0xFF666666),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        } else {
                            items(filteredTrucks) { truck ->
                                TruckItem(
                                    truck = truck,
                                    onClick = {

                                    }
                                )
                            }
                        }
                    }
                }


                item {
                    Text(
                        "All Routes",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                when {
                    routeState.isLoading -> {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally),
                                color = Color(0xFF0066CC)
                            )
                        }
                    }
                    routeState.error.isNotEmpty() -> {
                        item {
                            Text(
                                "Error: ${routeState.error}",
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    else -> {
                        val routes = routeState.success ?: emptyList()
                        if (routes.isEmpty()) {
                            item {
                                Text(
                                    "No routes found",
                                    color = Color(0xFF666666),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        } else {
                            items(routes) { route ->
                                RouteItem(
                                    route = route,
                                    onClick = { navController.navigate(Routes.RouteDetailsScren(routeId = route.id)) }
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
fun ActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(Color(0xFFF8F8F8))
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color(0xFF0066CC),
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF0066CC),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun TruckItem(truck: TruckModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .shadow(3.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocalShipping,
                contentDescription = null,
                tint = Color(0xFF0066CC),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Truck #: ${truck.truckNumber}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        fontSize = 16.sp
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
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .shadow(3.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Map,
                contentDescription = null,
                tint = Color(0xFF0066CC),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Route: ${route.name}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                )
                Text(
                    "Areas: ${route.areaList.joinToString()}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF666666),
                        fontSize = 14.sp
                    )
                )
                Text(
                    "Active: ${if (route.isActive) "Yes" else "No"}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (route.isActive) Color(0xFF388E3C) else Color(0xFFD32F2F),
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}