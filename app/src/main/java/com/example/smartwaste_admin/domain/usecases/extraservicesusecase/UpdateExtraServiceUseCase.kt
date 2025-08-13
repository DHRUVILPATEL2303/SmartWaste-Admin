package com.example.smartwaste_admin.domain.usecases.extraservicesusecase

import com.example.smartwaste_admin.domain.repo.extraservicerepo.ExtraServiceRepositry
import javax.inject.Inject

class UpdateExtraServiceUseCase @Inject constructor(
    private val extraServicesRepositry: ExtraServiceRepositry

) {
    suspend fun updateExtraService(userId:String,id:String,status:String)=extraServicesRepositry.updateStatusExtraService(userId,id,status)
}