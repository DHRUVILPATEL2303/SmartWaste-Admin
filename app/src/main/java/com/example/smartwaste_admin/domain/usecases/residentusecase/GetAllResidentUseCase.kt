package com.example.smartwaste_admin.domain.usecases.residentusecase

import com.example.smartwaste_admin.domain.repo.residentrepo.ResidentRepository
import javax.inject.Inject

class GetAllResidentUseCase @Inject constructor(
    private val residentRepository: ResidentRepository
) {
    suspend  fun getAllResident() = residentRepository.getAllresident()

}