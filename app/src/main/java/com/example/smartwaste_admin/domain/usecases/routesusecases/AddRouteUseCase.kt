package com.example.smartwaste_admin.domain.usecases.routesusecases

import com.example.smartwaste_admin.data.models.RouteModel
import com.example.smartwaste_admin.domain.repo.routesrepo.RoutesRepositry
import javax.inject.Inject

class AddRouteUseCase @Inject constructor(
    private val routeRepository: RoutesRepositry
) {
    suspend fun addRoute(route: RouteModel)=routeRepository.addRoutes(route)
}