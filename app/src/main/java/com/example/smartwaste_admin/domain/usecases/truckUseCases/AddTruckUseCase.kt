package com.example.smartwaste_admin.domain.usecases.truckUseCases

import com.example.smartwaste_admin.data.models.TruckModel
import com.example.smartwaste_admin.domain.repo.truckRepo.TrucksRepositry
import javax.inject.Inject

class AddTruckUseCase @Inject constructor(
    private val trucksRepositry: TrucksRepositry
) {
    suspend fun addTruck(truckModel: TruckModel)=trucksRepositry.AddTruck(truckModel)
}