package com.example.smartwaste_admin.domain.repo.workerrepo

import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.WorkerModel
import com.google.rpc.context.AttributeContext
import kotlinx.coroutines.flow.Flow

interface WorkersRepositry {

    suspend fun getAllWorkers() : Flow<ResultState<List<WorkerModel>>>

    suspend fun getWorkerById(id : String) : Flow<ResultState<WorkerModel>>


}