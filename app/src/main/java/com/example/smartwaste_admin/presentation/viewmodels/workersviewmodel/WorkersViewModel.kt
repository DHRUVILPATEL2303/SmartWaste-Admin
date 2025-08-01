package com.example.smartwaste_admin.presentation.viewmodels.workersviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.WorkerModel
import com.example.smartwaste_admin.domain.usecases.workersUseCase.GetAllWorkersUseCase
import com.example.smartwaste_admin.domain.usecases.workersUseCase.GetWorkerByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WorkersViewModel @Inject constructor(
    private val getWorkersUseCase: GetAllWorkersUseCase,
    private val getWorkerByIdUseCase: GetWorkerByIdUseCase
): ViewModel() {

    private val _allWorkersstate = MutableStateFlow(CommonWorkerState<List<WorkerModel>>())
    val allWorkersstate: StateFlow<CommonWorkerState<List<WorkerModel>>> = _allWorkersstate.asStateFlow()


    private val _workerState = MutableStateFlow(CommonWorkerState<WorkerModel>())
    val workerState: StateFlow<CommonWorkerState<WorkerModel>> = _workerState.asStateFlow()

    fun getAllWorkers(){
        viewModelScope.launch(Dispatchers.IO) {
            getWorkersUseCase.getAllWorkers().collect {
                when(it){
                    is ResultState.Success -> {
                        _allWorkersstate.value = CommonWorkerState(success = it.data)
                    }

                    is ResultState.Error -> {
                        _allWorkersstate.value = CommonWorkerState(error = it.error.toString())
                    }
                    is ResultState.Loading -> {
                        _allWorkersstate.value = CommonWorkerState(isLoading = true)
                    }


                }
            }

        }
    }

    fun getWorkerById(id: String){
        viewModelScope.launch(Dispatchers.IO) {

            getWorkerByIdUseCase.getWorkerByID(id).collect {
                when(it){
                    is ResultState.Success -> {
                        _workerState.value = CommonWorkerState(success = it.data)
                    }
                    is ResultState.Error -> {
                        _workerState.value = CommonWorkerState(error = it.error.toString())
                    }
                    is ResultState.Loading -> {
                        _workerState.value = CommonWorkerState(isLoading = true)
                    }
                }

            }
        }

    }



}

data class CommonWorkerState<T>(
    val isLoading: Boolean = false,
    val success: T? = null,
    val error: String = ""
)