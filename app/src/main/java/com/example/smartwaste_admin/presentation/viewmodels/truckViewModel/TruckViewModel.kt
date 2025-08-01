package com.example.smartwaste_admin.presentation.viewmodels.truckViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.TruckModel
import com.example.smartwaste_admin.domain.usecases.truckUseCases.AddTruckUseCase
import com.example.smartwaste_admin.domain.usecases.truckUseCases.DeleteTruckUseCase
import com.example.smartwaste_admin.domain.usecases.truckUseCases.GetAllTrucksUseCase
import com.google.android.gms.common.internal.service.Common
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TruckViewModel @Inject constructor(
    private val getAllTrucksUseCase: GetAllTrucksUseCase,
    private val addTruckUseCase: AddTruckUseCase,
    private val deleteTruckUseCase: DeleteTruckUseCase
) : ViewModel() {

    private val _allTrucksState = MutableStateFlow(CommonTruckState<List<TruckModel>>())
    val allTrucksState: StateFlow<CommonTruckState<List<TruckModel>>> =
        _allTrucksState.asStateFlow()

    private val _addTruckState = MutableStateFlow(CommonTruckState<String>())
    val addTruckState: StateFlow<CommonTruckState<String>> = _addTruckState.asStateFlow()


    private val _deleteTruckState = MutableStateFlow(CommonTruckState<String>())
    val deleteTruckState: StateFlow<CommonTruckState<String>> = _deleteTruckState.asStateFlow()


    fun getAllTrucks() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllTrucksUseCase.getAllTrucksUsecase().collect {
                when (it) {
                    is ResultState.Error -> {
                        _allTrucksState.value =
                            CommonTruckState(error = it.error.toString(), isLoading = false)
                    }

                    ResultState.Loading -> {
                        _allTrucksState.value = CommonTruckState(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _allTrucksState.value =
                            CommonTruckState(isLoading = false, success = it.data)
                    }
                }
            }
        }
    }

    fun addTruck(truckModel: TruckModel) {
        viewModelScope.launch(Dispatchers.IO) {
            _addTruckState.value = CommonTruckState(isLoading = true)
            val result = addTruckUseCase.addTruck(truckModel)
            when (result) {
                is ResultState.Error -> {
                    _addTruckState.value =
                        CommonTruckState(error = result.error.toString(), isLoading = false)
                }

                ResultState.Loading -> {
                    _addTruckState.value = CommonTruckState(isLoading = true)
                }

                is ResultState.Success -> {
                    _addTruckState.value =
                        CommonTruckState(isLoading = false, success = result.data)

                }


            }
        }
    }

    fun deleteTruck(truckId: String) {

        viewModelScope.launch(Dispatchers.IO) {

            _deleteTruckState.value = CommonTruckState(isLoading = true)
            val result = deleteTruckUseCase.deleteTruckusecase(truckId)

            when (result) {
                is ResultState.Error -> {
                    _deleteTruckState.value =
                        CommonTruckState(error = result.error.toString(), isLoading = false)
                }
                ResultState.Loading -> {
                    _deleteTruckState.value = CommonTruckState(isLoading = true)
                }

                is ResultState.Success -> {
                    _deleteTruckState.value =
                        CommonTruckState(isLoading = false, success = result.data)
                }
            }

        }

    }






}



data class CommonTruckState<T>(
    val isLoading: Boolean = false,
    val success: T? = null,
    val error: String = ""
)