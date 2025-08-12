package com.example.smartwaste_admin.presentation.screens.routeprogressscreens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.smartwaste_admin.data.models.AreaProgress
import com.example.smartwaste_admin.data.models.RouteProgressModel
import com.example.smartwaste_admin.presentation.viewmodels.routeprogressviewmodel.RouteProgressViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteProgressScreenUI(
    modifier: Modifier = Modifier,
    viewModel: RouteProgressViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val routeProgressState by viewModel.routeProgressState.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("All") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var expandedRouteId by remember { mutableStateOf<String?>(null) }

    // Auto-refresh every 30 seconds
    LaunchedEffect(Unit) {
        viewModel.getAllRoutesProgress()

        // Auto refresh every 30 seconds
        kotlinx.coroutines.delay(30000)
        viewModel.getAllRoutesProgress()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FA),
                        Color(0xFFE9ECEF)
                    )
                )
            )
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Route Progress",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Live tracking • Updated now",
                        fontSize = 12.sp,
                        color = Color(0xFF6C757D)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color(0xFF2E7D32)
            ),
            actions = {
                // Refresh Button
                IconButton(onClick = { viewModel.getAllRoutesProgress() }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color(0xFF2E7D32)
                    )
                }

                // Filter Button
                Box {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = Color(0xFF2E7D32)
                        )
                    }

                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        listOf("All", "In Progress", "Completed", "Not Started").forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter) },
                                onClick = {
                                    selectedFilter = filter
                                    showFilterMenu = false
                                },
                                leadingIcon = {
                                    if (selectedFilter == filter) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        )

        // Statistics Cards
        routeProgressState.success?.let { routes ->
            val totalRoutes = routes.size
            val completedRoutes = routes.count { it.isRouteCompleted }
            val inProgressRoutes = routes.count { route ->
                !route.isRouteCompleted && route.areaProgress.any { it.isCompleted }
            }
            val notStartedRoutes = routes.count { route ->
                !route.isRouteCompleted && route.areaProgress.none { it.isCompleted }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    StatCard(
                        title = "Total Routes",
                        count = totalRoutes,
                        icon = Icons.Default.Route,
                        color = Color(0xFF1976D2),
                        gradient = listOf(Color(0xFF1976D2), Color(0xFF1565C0))
                    )
                }
                item {
                    StatCard(
                        title = "In Progress",
                        count = inProgressRoutes,
                        icon = Icons.Default.Pending,
                        color = Color(0xFFFF9800),
                        gradient = listOf(Color(0xFFFF9800), Color(0xFFFF8F00))
                    )
                }
                item {
                    StatCard(
                        title = "Completed",
                        count = completedRoutes,
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF4CAF50),
                        gradient = listOf(Color(0xFF4CAF50), Color(0xFF43A047))
                    )
                }
                item {
                    StatCard(
                        title = "Not Started",
                        count = notStartedRoutes,
                        icon = Icons.Default.Schedule,
                        color = Color(0xFF757575),
                        gradient = listOf(Color(0xFF757575), Color(0xFF616161))
                    )
                }
            }
        }

        // Content Area
        when {
            routeProgressState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Color(0xFF2E7D32),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading route progress...",
                            fontSize = 16.sp,
                            color = Color(0xFF6C757D)
                        )
                    }
                }
            }

            routeProgressState.error.isNotEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFE57373),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Failed to load routes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF424242)
                        )
                        Text(
                            text = routeProgressState.error,
                            fontSize = 14.sp,
                            color = Color(0xFF757575),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.getAllRoutesProgress() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32)
                            )
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Try Again")
                        }
                    }
                }
            }

            else -> {
                val filteredRoutes = routeProgressState.success?.let { routes ->
                    when (selectedFilter) {
                        "Completed" -> routes.filter { it.isRouteCompleted }
                        "In Progress" -> routes.filter { route ->
                            !route.isRouteCompleted && route.areaProgress.any { it.isCompleted }
                        }
                        "Not Started" -> routes.filter { route ->
                            !route.isRouteCompleted && route.areaProgress.none { it.isCompleted }
                        }
                        else -> routes
                    }
                } ?: emptyList()

                if (filteredRoutes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Route,
                                contentDescription = null,
                                tint = Color(0xFFBDBDBD),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (selectedFilter == "All") "No routes found" else "No $selectedFilter routes",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF757575)
                            )
                            Text(
                                text = "Routes will appear here once they are created",
                                fontSize = 14.sp,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredRoutes) { route ->
                            RouteProgressCard(
                                route = route,
                                isExpanded = expandedRouteId == route.routeId,
                                onExpandClick = {
                                    expandedRouteId = if (expandedRouteId == route.routeId) null else route.routeId
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
private fun StatCard(
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    gradient: List<Color>
) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(gradient),
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(vertical = 8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RouteProgressCard(
    route: RouteProgressModel,
    isExpanded: Boolean,
    onExpandClick: () -> Unit
) {
    val completedAreas = route.areaProgress.count { it.isCompleted }
    val totalAreas = route.areaProgress.size
    val progressPercentage = if (totalAreas > 0) (completedAreas * 100) / totalAreas else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RouteStatusIndicator(
                        isCompleted = route.isRouteCompleted,
                        hasProgress = completedAreas > 0
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Route ${route.routeId.take(8)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF212529)
                        )
                        Text(
                            text = route.date.ifEmpty { "Today" },
                            fontSize = 12.sp,
                            color = Color(0xFF6C757D)
                        )
                    }
                }

                IconButton(onClick = onExpandClick) {
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF495057)
                    )
                    Text(
                        text = "$completedAreas/$totalAreas areas",
                        fontSize = 12.sp,
                        color = Color(0xFF6C757D)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = if (totalAreas > 0) completedAreas.toFloat() / totalAreas else 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when {
                        route.isRouteCompleted -> Color(0xFF4CAF50)
                        progressPercentage > 50 -> Color(0xFF2196F3)
                        progressPercentage > 0 -> Color(0xFFFF9800)
                        else -> Color(0xFFE0E0E0)
                    },
                    trackColor = Color(0xFFE9ECEF)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$progressPercentage% completed",
                    fontSize = 12.sp,
                    color = Color(0xFF6C757D)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Team Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TeamMemberInfo(
                    icon = Icons.Default.Person,
                    label = "Collector",
                    id = route.assignedCollectorId,
                    modifier = Modifier.weight(1f)
                )
                TeamMemberInfo(
                    icon = Icons.Default.DriveEta,
                    label = "Driver",
                    id = route.assignedDriverId,
                    modifier = Modifier.weight(1f)
                )
                TeamMemberInfo(
                    icon = Icons.Default.LocalShipping,
                    label = "Truck",
                    id = route.assignedTruckId,
                    modifier = Modifier.weight(1f)
                )
            }

            // Expandable Area Details
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFE9ECEF))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Area Progress Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF495057)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    route.areaProgress.forEach { area ->
                        AreaProgressItem(area = area)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Last Updated
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF6C757D),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Last updated: ${formatTimestamp(route.lastUpdated)}",
                            fontSize = 11.sp,
                            color = Color(0xFF6C757D)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteStatusIndicator(
    isCompleted: Boolean,
    hasProgress: Boolean
) {
    val (color, icon) = when {
        isCompleted -> Color(0xFF4CAF50) to Icons.Default.CheckCircle
        hasProgress -> Color(0xFFFF9800) to Icons.Default.Pending
        else -> Color(0xFF757575) to Icons.Default.Schedule
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color.copy(alpha = 0.1f), CircleShape)
            .border(2.dp, color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun TeamMemberInfo(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    id: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF2E7D32),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF6C757D)
        )
        Text(
            text = if (id.isNotEmpty()) id.take(6) else "N/A",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF495057),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AreaProgressItem(area: AreaProgress) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (area.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (area.isCompleted) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = area.areaName.ifEmpty { "Area ${area.areaId.take(6)}" },
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (area.isCompleted) Color(0xFF495057) else Color(0xFF6C757D)
            )
            if (area.isCompleted && area.completedAt != null) {
                Text(
                    text = "Completed at ${formatTimestamp(area.completedAt!!)}",
                    fontSize = 11.sp,
                    color = Color(0xFF4CAF50)
                )
            }
        }

        if (area.isCompleted) {
            Icon(
                Icons.Default.Done,
                contentDescription = "Completed",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}