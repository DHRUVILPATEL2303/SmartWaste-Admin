package com.example.smartwaste_admin.presentation.viewmodels.routeprogressviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.RouteProgressModel
import com.example.smartwaste_admin.domain.usecases.routeprogressusecase.GetAllRoutesProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RouteProgressViewModel @Inject constructor(
    private val getAllRoutesProgressUseCase: GetAllRoutesProgressUseCase
) : ViewModel(){


    private val _routeProgressState = MutableStateFlow(CommonRouteProgressState<List<RouteProgressModel>>())
    val routeProgressState = _routeProgressState.asStateFlow()

    fun getAllRoutesProgress(){
        viewModelScope.launch {
            getAllRoutesProgressUseCase.getAllRoutesProgress().collect{

                when(it){
                    is ResultState.Loading -> {
                        _routeProgressState.value = CommonRouteProgressState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _routeProgressState.value = CommonRouteProgressState(success = it.data)
                    }

                    is ResultState.Error -> {
                        _routeProgressState.value = CommonRouteProgressState(error = it.error)
                    }

                }
            }

        }
    }


}

data class CommonRouteProgressState<T>(
    val isLoading : Boolean = false,
    val success : T?=null,
    val error : String = ""
)