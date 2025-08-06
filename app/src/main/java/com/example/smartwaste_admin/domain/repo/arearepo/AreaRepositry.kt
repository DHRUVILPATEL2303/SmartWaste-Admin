package com.example.smartwaste_admin.domain.repo.arearepo

import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.AreaModel
import kotlinx.coroutines.flow.Flow

interface AreaRepositry {


    suspend fun getAllAreas() : Flow<ResultState<List<AreaModel>>>

    suspend fun addArea(area : AreaModel) : ResultState<String>
}