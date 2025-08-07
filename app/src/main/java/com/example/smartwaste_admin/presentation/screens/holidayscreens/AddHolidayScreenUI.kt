package com.example.smartwaste_admin.presentation.screens.holidayscreens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartwaste_admin.data.models.HolidayModel
import com.example.smartwaste_admin.presentation.viewmodels.holidayViewModel.HolidayViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHolidayScreenUI(
    modifier: Modifier = Modifier,
    viewModel: HolidayViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val addState by viewModel.addHolidayState.collectAsState()

    var holidayName by remember { mutableStateOf("") }
    var holidayDate by remember { mutableStateOf("") }

    val isValid = holidayName.isNotBlank() && holidayDate.isNotBlank() && isValidDate(holidayDate)



    LaunchedEffect(addState.succcess) {
        addState.succcess?.let {
            Toast.makeText(context, "Holiday added successfully", Toast.LENGTH_SHORT).show()
                navController.popBackStack()


        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Holiday") }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = holidayName,
                onValueChange = { holidayName = it },
                label = { Text("Holiday Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Enter the name of the holiday" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            HolidayDatePicker(
                selectedDate = holidayDate,
                onDateSelected = { holidayDate = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val model = HolidayModel(
                        holidayName = holidayName.trim(),
                        holidayDate = holidayDate.trim()
                    )
                    viewModel.addHoliday(model)
                },
                enabled = isValid && !addState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (addState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Add Holiday")
                }
            }

            if (addState.error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(addState.error, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun HolidayDatePicker(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val dateFormatter = remember {
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            readOnly = true,
            label = { Text("Holiday Date") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Button(
            onClick = {
                val datePickerDialog = android.app.DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        onDateSelected(dateFormatter.format(calendar.time))
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Select Date")
        }
    }
}
fun isValidDate(date: String): Boolean {
    return try {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        sdf.isLenient = false
        sdf.parse(date)
        true
    } catch (e: Exception) {
        false
    }
}