package com.example.smartwaste_admin.domain.usecases.routesusecases

import com.example.smartwaste_admin.domain.repo.routesrepo.RoutesRepositry
import javax.inject.Inject

class GetAllRouteUseCase @Inject constructor(
    private val routeRepository: RoutesRepositry
){

    suspend fun getAllRoutes()=routeRepository.getAllRoutes()
}