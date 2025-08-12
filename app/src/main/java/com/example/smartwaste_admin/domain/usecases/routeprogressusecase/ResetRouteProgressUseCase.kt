package com.example.smartwaste_admin.domain.usecases.routeprogressusecase

import com.example.smartwaste_admin.domain.repo.routeprogressrepositry.RouteProgressRepositry
import javax.inject.Inject

class ResetRouteProgressUseCase @Inject constructor(
    private val routeProgressRepositry: RouteProgressRepositry
) {

    suspend fun resetRouteProgress()=routeProgressRepositry.forceResetAllRoutes()
}