package com.example.smartwaste_admin.domain.usecases.areaUseCase

import com.example.smartwaste_admin.domain.repo.arearepo.AreaRepositry
import javax.inject.Inject

class GetAreaDetailsUseCase @Inject constructor(
    private val areaRepositry: AreaRepositry
) {

    suspend fun getAreaDetails(query : String)=areaRepositry.getAddressDetails(query)
}