package com.example.smartwaste_admin.presentation.screens.extraservice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.smartwaste_admin.data.models.ExtraServiceModel
import com.example.smartwaste_admin.presentation.viewmodels.extraserviceviewmodel.ExtraServiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraServiceScreenUI(
    modifier: Modifier = Modifier,
    viewModel: ExtraServiceViewModel = hiltViewModel()
) {
    val extraServiceState by viewModel.extraServiceState.collectAsStateWithLifecycle()
    val updateServiceState by viewModel.updateExtraServiceState.collectAsStateWithLifecycle()
    val deleteServiceState by viewModel.deleteExtraServiceState.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("All") }
    var showDeleteDialog by remember { mutableStateOf<ExtraServiceModel?>(null) }
    var showUpdateDialog by remember { mutableStateOf<ExtraServiceModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllServices()
    }

    LaunchedEffect(updateServiceState.success) {
        if (updateServiceState.success?.isNotEmpty() == true) {
            viewModel.getAllServices()
            showUpdateDialog = null
        }
    }

    LaunchedEffect(deleteServiceState.success) {
        if (deleteServiceState.success?.isNotEmpty() == true) {
            viewModel.getAllServices() // Refresh the list
            showDeleteDialog = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Extra Services",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            StatusFilterChips(
                selectedFilter = selectedFilter,
                onFilterChanged = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                extraServiceState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                extraServiceState.error.isNotEmpty() -> {
                    ErrorMessage(
                        error = extraServiceState.error,
                        onRetry = { viewModel.getAllServices() }
                    )
                }

                extraServiceState.success != null -> {
                    val filteredServices = filterServices(extraServiceState.success!!, selectedFilter)

                    if (filteredServices.isEmpty()) {
                        EmptyState(filter = selectedFilter)
                    } else {
                        ServicesList(
                            services = filteredServices,
                            onUpdateService = { service -> showUpdateDialog = service },
                            onDeleteService = { service -> showDeleteDialog = service }
                        )
                    }
                }
            }
        }
    }

    showUpdateDialog?.let { service ->
        UpdateServiceDialog(
            service = service,
            isLoading = updateServiceState.isLoading,
            onDismiss = { showUpdateDialog = null },
            onConfirm = { newStatus ->
                viewModel.updateExtraService(service.userId, service.id, newStatus)
            }
        )
    }

    showDeleteDialog?.let { service ->
        DeleteServiceDialog(
            service = service,
            isLoading = deleteServiceState.isLoading,
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                viewModel.deleteExtraService(service.userId, service.id)
            }
        )
    }
}

@Composable
private fun StatusFilterChips(
    selectedFilter: String,
    onFilterChanged: (String) -> Unit
) {
    val filters = listOf("All", "Pending", "Approved", "Completed", "Rejected")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                onClick = { onFilterChanged(filter) },
                label = { Text(filter) },
                selected = selectedFilter == filter,
                leadingIcon = if (selectedFilter == filter) {
                    { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

@Composable
private fun ServicesList(
    services: List<ExtraServiceModel>,
    onUpdateService: (ExtraServiceModel) -> Unit,
    onDeleteService: (ExtraServiceModel) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(services) { service ->
            ServiceCard(
                service = service,
                onUpdateClick = { onUpdateService(service) },
                onDeleteClick = { onDeleteService(service) }
            )
        }
    }
}

@Composable
private fun ServiceCard(
    service: ExtraServiceModel,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(status = service.status)

                Row {
                    IconButton(onClick = onUpdateClick) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Update Status",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Service",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ServiceDetailRow(
                icon = Icons.Default.LocationOn,
                label = "Area",
                value = service.areaName
            )

            ServiceDetailRow(
                icon = Icons.Default.Route,
                label = "Route",
                value = service.routeName
            )

            ServiceDetailRow(
                icon = Icons.Default.Home,
                label = "Address",
                value = service.address
            )

            ServiceDetailRow(
                icon = Icons.Default.DateRange,
                label = "Date",
                value = service.date
            )

            if (service.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Description:",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = service.description,
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ServiceDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatusChip(status: String) {
    val (backgroundColor, contentColor, statusText) = when (status.lowercase()) {
        "pending" -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            "Pending"
        )
        "approved" -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            "Approved"
        )
        "completed" -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "Completed"
        )
        "rejected" -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "Rejected"
        )
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            status
        )
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = statusText,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}

@Composable
private fun ErrorMessage(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error loading services",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyState(filter: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Assignment,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (filter == "All") "No services found" else "No $filter services",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Services will appear here when available",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun UpdateServiceDialog(
    service: ExtraServiceModel,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val statusOptions = listOf("Pending", "Approved", "Completed", "Rejected")
    var selectedStatus by remember { mutableStateOf(service.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Service Status") },
        text = {
            Column {
                Text(
                    text = "Update status for service in ${service.areaName}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                statusOptions.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = status)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedStatus) },
                enabled = !isLoading && selectedStatus != service.status
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Update")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DeleteServiceDialog(
    service: ExtraServiceModel,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Service") },
        text = {
            Text("Are you sure you want to delete this service from ${service.areaName}? This action cannot be undone.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onError
                    )
                } else {
                    Text("Delete")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun filterServices(services: List<ExtraServiceModel>, filter: String): List<ExtraServiceModel> {
    return if (filter == "All") {
        services
    } else {
        services.filter { it.status.equals(filter, ignoreCase = true) }
    }
}