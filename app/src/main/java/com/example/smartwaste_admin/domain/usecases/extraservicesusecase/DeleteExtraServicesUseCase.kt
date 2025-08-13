package com.example.smartwaste_admin.domain.usecases.extraservicesusecase

import com.example.smartwaste_admin.domain.repo.extraservicerepo.ExtraServiceRepositry
import javax.inject.Inject

class DeleteExtraServicesUseCase @Inject constructor(
    private val extraServicesRepositry: ExtraServiceRepositry
) {

    suspend fun deleteExtraSerices(userId:String,id:String)=extraServicesRepositry.deleteExtraService(userId,id)
}