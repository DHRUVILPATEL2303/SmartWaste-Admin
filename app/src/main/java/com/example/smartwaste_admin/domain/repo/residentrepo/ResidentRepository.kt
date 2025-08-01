package com.example.smartwaste_admin.domain.repo.residentrepo

import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.ResidentModel
import kotlinx.coroutines.flow.Flow

interface ResidentRepository {

    suspend fun getAllresident() : Flow<ResultState<List<ResidentModel>>>

    suspend fun getResidentById(id: String) : Flow<ResultState<ResidentModel>>
}