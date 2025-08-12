package com.example.smartwaste_admin.domain.repo.routeprogressrepositry

import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.RouteProgressModel
import kotlinx.coroutines.flow.Flow

interface RouteProgressRepositry {

    suspend fun getAllRoutesProgress() : Flow<ResultState<List<RouteProgressModel>>>
}