package com.example.smartwaste_admin.domain.repo.truckRepo

import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.TruckModel
import kotlinx.coroutines.flow.Flow

interface TrucksRepositry {

    suspend fun getAllTrucks() : Flow<ResultState<List<TruckModel>>>

    suspend fun AddTruck(truckModel: TruckModel): ResultState<String>

    suspend fun deleteTruck(truckId: String): ResultState<String>


}