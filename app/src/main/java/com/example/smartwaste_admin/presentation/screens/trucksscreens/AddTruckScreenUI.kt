package com.example.smartwaste_admin.presentation.screens.trucksscreens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.smartwaste_admin.data.models.TruckModel
import com.example.smartwaste_admin.presentation.viewmodels.truckViewModel.TruckViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTruckScreenUI(
    modifier: Modifier = Modifier,
    viewModel: TruckViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val context = LocalContext.current
    var truckNumber by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var routeId by remember { mutableStateOf("") }

    val addTruckState = viewModel.addTruckState.collectAsState().value

    LaunchedEffect(addTruckState.success) {
        if (addTruckState.success != null) {
            Toast.makeText(context, "Truck Added Successfully", Toast.LENGTH_SHORT).show()
            truckNumber = ""

        } else if (addTruckState.error.isNotEmpty()) {
            Toast.makeText(context, "Error: ${addTruckState.error}", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Truck") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            OutlinedTextField(
                value = truckNumber,
                onValueChange = { truckNumber = it },
                label = { Text("Truck Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (truckNumber.isNotBlank() ) {
                        val truck = TruckModel(
                            truckNumber = truckNumber.trim(),

                        )
                        viewModel.addTruck(truck)
                    } else {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !addTruckState.isLoading,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add Truck")
            }

            if (addTruckState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}