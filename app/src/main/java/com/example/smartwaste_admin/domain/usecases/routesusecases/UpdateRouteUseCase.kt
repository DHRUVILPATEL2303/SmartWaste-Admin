package com.example.smartwaste_admin.domain.usecases.routesusecases

import com.example.smartwaste_admin.data.models.RouteModel
import com.example.smartwaste_admin.domain.repo.routesrepo.RoutesRepositry
import javax.inject.Inject

class UpdateRouteUseCase @Inject constructor(
    private val routesRepository: RoutesRepositry
) {

    suspend fun updateRouteUseCase(route: RouteModel)= routesRepository.updateRoutes(route)
}