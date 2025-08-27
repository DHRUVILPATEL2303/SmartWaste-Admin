package com.example.smartwaste_admin.presentation.screens.areascreens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.example.smartwaste_admin.data.models.NominatimPlace
import com.example.smartwaste_admin.presentation.viewmodels.areaviewmodel.AreaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAreaScreenUI(
    viewModel: AreaViewModel = hiltViewModel(),
    navController: NavHostController
) {
    var areaName by remember { mutableStateOf("") }
    val addAreaState = viewModel.addAreaState.collectAsState().value
    val areaDetailsState = viewModel.getAreaDetailsState.collectAsState().value

    var expanded by remember { mutableStateOf(false) }
    var selectedPlace by remember { mutableStateOf<NominatimPlace?>(null) }

    Log.d("AddAreaScreenUI", "areadetailscsreen : $areaDetailsState")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Area", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = areaName,
                    onValueChange = {
                        areaName = it

                        expanded = false
                        selectedPlace = null
                    },
                    label = { Text("Area Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (areaName.trim().length >= 3) {
                                    viewModel.getAreaDetails(areaName.trim())
                                    expanded = true
                                }
                            },
                            enabled = areaName.trim().length >= 3 && !areaDetailsState.isLoading
                        ) {
                            if (areaDetailsState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = if (areaName.trim().length >= 3)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                )


                DropdownMenu(
                    expanded = expanded &&
                            !areaDetailsState.isLoading &&
                            areaDetailsState.success?.isNotEmpty() == true,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    areaDetailsState.success?.take(10)?.forEach { place ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = place.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2
                                )
                            },
                            onClick = {
                                selectedPlace = place

                                expanded = false
                            }
                        )
                    }
                }
            }

            if (areaName.isNotEmpty() && areaName.trim().length < 3) {
                Text(
                    "Enter at least 3 characters to search",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            if (areaDetailsState.isLoading) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Searching...", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(16.dp))
            }

            // Show search error if any
            if (areaDetailsState.error.isNotEmpty()) {
                Text(
                    "Search Error: ${areaDetailsState.error}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    selectedPlace?.let { place ->
                        viewModel.addArea(
                            AreaModel(
                                areaName = place.displayName,
                                latitude = place.lat.toDouble(),
                                longitude = place.lon.toDouble()
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedPlace != null && !addAreaState.isLoading
            ) {
                if (addAreaState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Add Area")
                }
            }

            Spacer(Modifier.height(24.dp))

            when {
                addAreaState.error.isNotEmpty() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            "Error: ${addAreaState.error}",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                addAreaState.success != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f))
                    ) {
                        Text(
                            "Success: ${addAreaState.success}",
                            color = Color.Green.copy(alpha = 0.8f),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            selectedPlace?.let { place ->
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Selected Location:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            place.displayName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Coordinates: ${place.lat}, ${place.lon}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}