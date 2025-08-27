package com.example.smartwaste_admin.domain.repo.arearepo

import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.AreaModel
import com.example.smartwaste_admin.data.models.NominatimPlace
import kotlinx.coroutines.flow.Flow

interface AreaRepositry {


    suspend fun getAllAreas() : Flow<ResultState<List<AreaModel>>>

    suspend fun addArea(area : AreaModel) : ResultState<String>

    suspend fun getAddressDetails(query : String) : Flow<ResultState<List<NominatimPlace>>>
}