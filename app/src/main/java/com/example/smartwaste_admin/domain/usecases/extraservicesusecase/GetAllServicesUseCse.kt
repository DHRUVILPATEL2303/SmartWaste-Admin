package com.example.smartwaste_admin.domain.usecases.extraservicesusecase

import com.example.smartwaste_admin.domain.repo.extraservicerepo.ExtraServiceRepositry
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class GetAllServicesUseCse @Inject constructor(
    private val extraServiceRepositry: ExtraServiceRepositry
) {

    suspend fun getAllServices()=extraServiceRepositry.getAllService()
}