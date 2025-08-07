package com.example.smartwaste_admin.presentation.screens.holidayscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartwaste_admin.data.models.HolidayModel
import com.example.smartwaste_admin.presentation.navigation.Routes
import com.example.smartwaste_admin.presentation.viewmodels.holidayViewModel.HolidayViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HolidayScreenUI(
    modifier: Modifier = Modifier,
    viewModel: HolidayViewModel = hiltViewModel(),
    navController: NavController
) {
    val holidayState by viewModel.allHolidayState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllHoliday()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Holidays",
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {

                        navController.navigate(Routes.AddHolidayScreen)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Holiday",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                holidayState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF0066CC)
                    )
                }

                holidayState.error.isNotEmpty() -> {
                    Text(
                        text = "Error: ${holidayState.error}",
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                holidayState.succcess.isNullOrEmpty() -> {
                    Text(
                        text = "No holidays available.",
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(12.dp)
                    ) {
                        items(holidayState.succcess?: emptyList<HolidayModel>()) { holiday ->
                            HolidayCard(holidayModel = holiday)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HolidayCard(holidayModel: HolidayModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8FF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = holidayModel.holidayName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Text(
                text = "Date: ${holidayModel.holidayDate}",
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
        }
    }
}