package com.example.smartwaste_admin.presentation.viewmodels.routesviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.RouteModel
import com.example.smartwaste_admin.domain.usecases.routesusecases.AddRouteUseCase
import com.example.smartwaste_admin.domain.usecases.routesusecases.GetAllRouteUseCase
import com.example.smartwaste_admin.domain.usecases.routesusecases.GetRouteByIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RoutesViewModel @Inject constructor(
    private val getAllRouteUseCase: GetAllRouteUseCase,
    private val getRouteByIdUseCase: GetRouteByIdUseCase,
    private val addRouteUseCase: AddRouteUseCase,
) : ViewModel() {

    private val _allRoutesState = MutableStateFlow(CommonRoutesState<List<RouteModel>>())
    val allRoutesState = _allRoutesState.asStateFlow()

    private val _routeByIdState = MutableStateFlow(CommonRoutesState<RouteModel>())
    val routeByIdState = _routeByIdState.asStateFlow()

    private val _addRouteState = MutableStateFlow(CommonRoutesState<String>())
    val addRouteState = _addRouteState.asStateFlow()


    fun getAllRoutes() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllRouteUseCase.getAllRoutes().collect {
                when (it) {
                    is ResultState.Success -> {
                        _allRoutesState.value = CommonRoutesState(success = it.data)

                    }

                    is ResultState.Loading -> {
                        _allRoutesState.value = CommonRoutesState(isLoading = true)
                    }

                    is ResultState.Error -> {
                        _allRoutesState.value = CommonRoutesState(error = it.error)
                    }

                }
            }

        }
    }

    fun getRouteById(id: String) {

        viewModelScope.launch(Dispatchers.IO) {
            getRouteByIdUseCase.getRouteById(id).collect {
                when (it) {
                    is ResultState.Success -> {
                        _routeByIdState.value = CommonRoutesState(success = it.data)
                    }

                    is ResultState.Loading -> {
                        _routeByIdState.value = CommonRoutesState(isLoading = true)
                    }

                    is ResultState.Error -> {
                        _routeByIdState.value = CommonRoutesState(error = it.error)
                    }

                }
            }
        }
    }

    fun addRoute(routeModel: RouteModel) {

        viewModelScope.launch(Dispatchers.IO) {
            _addRouteState.value = CommonRoutesState(isLoading = true)

            val result = addRouteUseCase.addRoute(routeModel)
            when (result) {
                is ResultState.Success -> {
                    _addRouteState.value = CommonRoutesState(success = result.data)
                }

                is ResultState.Loading -> {
                    _addRouteState.value = CommonRoutesState(isLoading = true)
                }

                is ResultState.Error -> {
                    _addRouteState.value = CommonRoutesState(error = result.error)
                }
            }
        }
    }


}


data class CommonRoutesState<T>(
    val isLoading: Boolean = false,
    val success: T? = null,
    val error: String = ""
)