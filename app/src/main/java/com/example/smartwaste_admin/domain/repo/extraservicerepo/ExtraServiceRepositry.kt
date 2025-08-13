package com.example.smartwaste_admin.domain.repo.extraservicerepo

import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.ExtraServiceModel
import kotlinx.coroutines.flow.Flow

interface ExtraServiceRepositry {


    suspend fun getAllService() : Flow<ResultState<List<ExtraServiceModel>>>

    suspend fun deleteExtraService(userId:String,id:String) : Flow<ResultState<String>>

    suspend fun updateStatusExtraService(userId: String,id : String,status : String) : Flow<ResultState<String>>
}