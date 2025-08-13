package com.example.smartwaste_admin.presentation.viewmodels.extraserviceviewmodel

import android.printservice.PrintService
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartwaste_admin.data.models.ExtraServiceModel
import com.example.smartwaste_admin.domain.usecases.extraservicesusecase.DeleteExtraServicesUseCase
import com.example.smartwaste_admin.domain.usecases.extraservicesusecase.GetAllServicesUseCse
import com.example.smartwaste_admin.domain.usecases.extraservicesusecase.UpdateExtraServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ExtraServiceViewModel @Inject constructor(
    private val getAllServicesUseCase: GetAllServicesUseCse,
    private val updateExtraServiceUseCase: UpdateExtraServiceUseCase,
    private val deleteExtraServicesUseCase: DeleteExtraServicesUseCase
) : ViewModel() {

    private val _extraServiceState =
        MutableStateFlow(CommonExtraServiceState<List<ExtraServiceModel>>())
    val extraServiceState = _extraServiceState.asStateFlow()

    private val _updateExtraServiceState = MutableStateFlow(CommonExtraServiceState<String>())
    val updateExtraServiceState = _updateExtraServiceState.asStateFlow()

    private val _deleteExtraServiceState = MutableStateFlow(CommonExtraServiceState<String>())
    val deleteExtraServiceState = _deleteExtraServiceState.asStateFlow()


    fun getAllServices() {
        Log.d("ExtraServiceViewModel", "getAllServices() called")
        viewModelScope.launch(Dispatchers.IO) {
            getAllServicesUseCase.getAllServices().collect { result ->
                Log.d("ExtraServiceViewModel", "Result received: $result")
                when (result) {
                    is com.example.smartwaste_admin.common.ResultState.Success -> {
                        Log.d("ExtraServiceViewModel", "Success: ${result.data?.size} services found")
                        _extraServiceState.value =
                            CommonExtraServiceState(success = result.data, isLoading = false)
                    }

                    is com.example.smartwaste_admin.common.ResultState.Loading -> {
                        Log.d("ExtraServiceViewModel", "Loading state")
                        _extraServiceState.value = CommonExtraServiceState(isLoading = true)
                    }

                    is com.example.smartwaste_admin.common.ResultState.Error -> {
                        Log.e("ExtraServiceViewModel", "Error: ${result.error}")
                        _extraServiceState.value =
                            CommonExtraServiceState(error = result.error, isLoading = false)
                    }
                }
            }
        }
    }

    fun updateExtraService(userId: String, id: String, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateExtraServiceUseCase.updateExtraService(userId, id, status).collect {


                when (it) {
                    is com.example.smartwaste_admin.common.ResultState.Success -> {
                        _updateExtraServiceState.value =
                            CommonExtraServiceState(success = it.data, isLoading = false)
                    }

                    is com.example.smartwaste_admin.common.ResultState.Loading -> {
                        _updateExtraServiceState.value = CommonExtraServiceState(isLoading = true)
                    }

                    is com.example.smartwaste_admin.common.ResultState.Error -> {
                        _updateExtraServiceState.value =
                            CommonExtraServiceState(error = it.error, isLoading = false)
                    }


                }
            }

        }
    }

    fun deleteExtraService(userId: String, id: String) {
        viewModelScope.launch(Dispatchers.IO) {

            deleteExtraServicesUseCase.deleteExtraSerices(userId, id).collect {


                when (it) {
                    is com.example.smartwaste_admin.common.ResultState.Success -> {
                        _deleteExtraServiceState.value =
                            CommonExtraServiceState(success = it.data, isLoading = false)
                    }

                    is com.example.smartwaste_admin.common.ResultState.Loading -> {
                        _deleteExtraServiceState.value = CommonExtraServiceState(isLoading = true)
                    }

                    is com.example.smartwaste_admin.common.ResultState.Error -> {
                        _deleteExtraServiceState.value =
                            CommonExtraServiceState(error = it.error, isLoading = false)
                    }


                }
            }

        }

    }


}

data class CommonExtraServiceState<T>(
    val isLoading: Boolean = false,
    val success: T? = null,
    val error: String = ""
)