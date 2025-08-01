package com.example.smartwaste_admin.domain.usecases.truckUseCases

import com.example.smartwaste_admin.domain.repo.truckRepo.TrucksRepositry
import javax.inject.Inject

class DeleteTruckUseCase @Inject constructor(
    private val truckRepository: TrucksRepositry
){
    suspend fun deleteTruckusecase(truckId: String)=truckRepository.deleteTruck(truckId = truckId)
}