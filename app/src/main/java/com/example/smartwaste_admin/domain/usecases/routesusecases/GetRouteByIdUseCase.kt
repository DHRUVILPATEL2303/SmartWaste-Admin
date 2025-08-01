package com.example.smartwaste_admin.domain.usecases.routesusecases

import com.example.smartwaste_admin.domain.repo.routesrepo.RoutesRepositry
import javax.inject.Inject

class GetRouteByIdUseCase @Inject constructor(
    private val routeRepository: RoutesRepositry
) {
   suspend fun getRouteById(id: String) = routeRepository.getRouteById(id)
}