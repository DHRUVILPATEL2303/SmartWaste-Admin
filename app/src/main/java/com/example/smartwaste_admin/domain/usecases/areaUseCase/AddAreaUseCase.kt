package com.example.smartwaste_admin.domain.usecases.areaUseCase

import com.example.smartwaste_admin.data.models.AreaModel
import com.example.smartwaste_admin.domain.repo.arearepo.AreaRepositry
import javax.inject.Inject

class AddAreaUseCase @Inject constructor(
    private val areaRepository: AreaRepositry
) {

    suspend fun addArea(area: AreaModel) = areaRepository.addArea(area)
}