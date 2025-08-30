package com.example.smartwaste_admin.presentation.screens.editorialscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.smartwaste_admin.data.models.EdiorialModel
import com.example.smartwaste_admin.presentation.viewmodels.ediorialviewmodel.EditorialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditorialScreenUI(
    navController: NavHostController,
    viewModel: EditorialViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var thumbnail by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    val editorialState by viewModel.addEditorialState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Add Editorial")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = thumbnail,
                onValueChange = { thumbnail = it },
                label = { Text("Thumbnail URL") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = videoUrl,
                onValueChange = { videoUrl = it },
                label = { Text("Video URL") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.addEditorial(
                        EdiorialModel(
                            title = title,
                            thumbnail = thumbnail,
                            vidoeUrl = videoUrl
                        )
                    )
                },
                enabled = !editorialState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Editorial")
            }

            if (editorialState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if (editorialState.error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = editorialState.error,
                    color = MaterialTheme.colors.error
                )
            }
            if (editorialState.success != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Editorial added successfully!",
                    color = MaterialTheme.colors.primary
                )

            }
        }
    }


}