package com.example.smartwaste_admin.presentation.viewmodels.areaviewmodel

import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.DisableContentCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.AreaModel
import com.example.smartwaste_admin.domain.usecases.areaUseCase.AddAreaUseCase
import com.example.smartwaste_admin.domain.usecases.areaUseCase.GetAllAreasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AreaViewModel @Inject constructor(
    private val getAllAreasUseCase: GetAllAreasUseCase,
    private val addAreaUseCase: AddAreaUseCase
) : ViewModel() {
    private val _allAreaState = MutableStateFlow(CommonAreaState<List<AreaModel>>())
    val allAreaState: StateFlow<CommonAreaState<List<AreaModel>>> = _allAreaState.asStateFlow()

    private val _addAreaState = MutableStateFlow(CommonAreaState<String>())
    val addAreaState: StateFlow<CommonAreaState<String>> = _addAreaState.asStateFlow()


    fun getAllAreas() {

        viewModelScope.launch(Dispatchers.IO) {
            getAllAreasUseCase.getAllAreas().collect {
                when (it) {
                    is ResultState.Loading -> {
                        _allAreaState.value = CommonAreaState(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _allAreaState.value = CommonAreaState(success = it.data)
                    }

                    is ResultState.Error -> {
                        _allAreaState.value = CommonAreaState(error = it.error)
                    }

                }
            }


        }

    }

    fun addArea(areaModel: AreaModel) {
        viewModelScope.launch(Dispatchers.IO) {

            _addAreaState.value = CommonAreaState(isLoading = true)

            val result = addAreaUseCase.addArea(areaModel)

            when (result) {
                is ResultState.Success -> {
                    _addAreaState.value = CommonAreaState(success = result.data)
                }

                is ResultState.Error -> {
                    _addAreaState.value = CommonAreaState(error = result.error)
                }

                else -> {}

            }


        }

    }


}


data class CommonAreaState<T>(
    val isLoading: Boolean = false,
    val success: T? = null,
    val error: String = ""

)