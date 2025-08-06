package com.example.smartwaste_admin.domain.usecases.areaUseCase

import com.example.smartwaste_admin.domain.repo.arearepo.AreaRepositry
import javax.inject.Inject

class GetAllAreasUseCase @Inject constructor(
    private val areaRepository: AreaRepositry
)
{

    suspend fun getAllAreas() = areaRepository.getAllAreas()
}