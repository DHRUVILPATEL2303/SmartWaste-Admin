package com.example.smartwaste_admin.domain.repo.routesrepo

import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.RouteModel
import kotlinx.coroutines.flow.Flow

interface RoutesRepositry {

    suspend fun getAllRoutes() : Flow<ResultState<List<RouteModel>>>

    suspend fun getRouteById(id : String) : Flow<ResultState<RouteModel>>

    suspend fun addRoutes(route : RouteModel) : ResultState<String>

    suspend fun updateRoutes(route : RouteModel) : ResultState<String>
}