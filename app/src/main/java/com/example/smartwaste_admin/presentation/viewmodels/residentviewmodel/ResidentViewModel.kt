package com.example.smartwaste_admin.presentation.viewmodels.residentviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.ResidentModel
import com.example.smartwaste_admin.domain.usecases.residentusecase.GetAllResidentUseCase
import com.example.smartwaste_admin.domain.usecases.residentusecase.getResidentByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResidentViewModel @Inject constructor(
    private val getAllResidentUseCase: GetAllResidentUseCase,
    private val getResidentByIdUseCase: getResidentByIdUseCase
) : ViewModel() {

    private val _allResidentState = MutableStateFlow(CommonResidentState<List<ResidentModel>>())
    val allResidentState: StateFlow<CommonResidentState<List<ResidentModel>>> = _allResidentState.asStateFlow()

    private val _residentState = MutableStateFlow(CommonResidentState<ResidentModel>())
    val residentState: StateFlow<CommonResidentState<ResidentModel>> = _residentState.asStateFlow()

    fun getAllResidents() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllResidentUseCase.getAllResident().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _allResidentState.value = CommonResidentState(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _allResidentState.value = CommonResidentState(data = result.data)
                    }

                    is ResultState.Error -> {
                        _allResidentState.value = CommonResidentState(error = result.error)
                    }
                }
            }
        }
    }

    fun getResidentById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getResidentByIdUseCase.getResidentById(id).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _residentState.value = CommonResidentState(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _residentState.value = CommonResidentState(data = result.data)
                    }

                    is ResultState.Error -> {
                        _residentState.value = CommonResidentState(error = result.error)
                    }
                }
            }
        }
    }
}

data class CommonResidentState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String = ""
)