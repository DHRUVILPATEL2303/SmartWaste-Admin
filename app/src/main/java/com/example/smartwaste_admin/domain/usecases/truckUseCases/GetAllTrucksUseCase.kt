package com.example.smartwaste_admin.domain.usecases.truckUseCases

import com.example.smartwaste_admin.domain.repo.truckRepo.TrucksRepositry
import javax.inject.Inject

class GetAllTrucksUseCase @Inject constructor(
    private val trucksRepositry: TrucksRepositry
){
    suspend fun getAllTrucksUsecase() =trucksRepositry.getAllTrucks()
}