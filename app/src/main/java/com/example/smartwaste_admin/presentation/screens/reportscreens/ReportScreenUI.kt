package com.example.smartwaste_admin.presentation.screens.reportscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.smartwaste_admin.data.models.ReportModel
import com.example.smartwaste_admin.presentation.viewmodels.reportsviewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreenUI(
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val reportState by viewModel.reportState.collectAsStateWithLifecycle()
    val deleteReportState by viewModel.deleteReportState.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var reportToDelete by remember { mutableStateOf<ReportModel?>(null) }
    var selectedFilter by remember { mutableStateOf("All") }
    var showFilterMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getAllReports()
    }

    LaunchedEffect(deleteReportState.success) {
        if (deleteReportState.success != null) {
            viewModel.getAllReports()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Reports Management",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color(0xFF2E7D32)
            ),
            actions = {
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
                        listOf("All", "Pending", "In Progress", "Completed").forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter) },
                                onClick = {
                                    selectedFilter = filter
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Reports",
                count = reportState.success?.size ?: 0,
                color = Color(0xFF1976D2),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Pending",
                count = reportState.success?.count { it.status == "Pending" } ?: 0,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Resolved",
                count = reportState.success?.count { it.status == "Resolved" } ?: 0,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }

        when {
            reportState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading reports...")
                    }
                }
            }

            reportState.error.isNotEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error: ${reportState.error}",
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.getAllReports() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32)
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {
                val filteredReports = reportState.success?.let { reports ->
                    if (selectedFilter == "All") reports
                    else reports.filter { it.status == selectedFilter }
                } ?: emptyList()

                if (filteredReports.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Assignment,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (selectedFilter == "All") "No reports found" else "No $selectedFilter reports",
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredReports) { report ->
                            ReportCard(
                                report = report,
                                onDeleteClick = {
                                    reportToDelete = report
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && reportToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Report") },
            text = { Text("Are you sure you want to delete this report? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        reportToDelete?.let { viewModel.deleteREport(it.reportId) }
                        showDeleteDialog = false
                        reportToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ReportCard(
    report: ReportModel,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Assignment,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Report #${report.reportId.take(8)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(status = report.status)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    icon = Icons.Default.LocationOn,
                    label = "Area",
                    value = report.areaName.ifEmpty { "N/A" },
                    modifier = Modifier.weight(1f)
                )
                InfoItem(
                    icon = Icons.Default.Route,
                    label = "Route",
                    value = if (report.routeId.isNotEmpty()) "Route ${report.routeId.take(6)}" else "N/A",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))



            if (report.description.isNotEmpty()) {
                Text(
                    text = report.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = report.reportDate.ifEmpty { "Unknown date" },
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                if (report.attachments.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Attachment,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${report.attachments.size} attachment${if (report.attachments.size > 1) "s" else ""}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF2E7D32),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "pending" -> Color(0xFFFFF3E0) to Color(0xFFFF9800)
        "in progress" -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        "resolved" -> Color(0xFFE8F5E8) to Color(0xFF4CAF50)
        "closed" -> Color(0xFFE0E0E0) to Color(0xFF757575)
        else -> Color(0xFFE0E0E0) to Color(0xFF757575)
    }

    Box(
        modifier = Modifier
            .background(
                backgroundColor,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}