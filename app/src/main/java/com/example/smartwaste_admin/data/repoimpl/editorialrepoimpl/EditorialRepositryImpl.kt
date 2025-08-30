package com.example.smartwaste_admin.data.repoimpl.editorialrepoimpl

import com.example.smartwaste_admin.common.EDITORIAL_PATH
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.EdiorialModel
import com.example.smartwaste_admin.domain.repo.editorialrepo.EditorialRepositry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EditorialRepositryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : EditorialRepositry{
    override suspend fun addEditorial(ediorialModel: EdiorialModel): ResultState<String> {
        return try {
            firestore.collection(EDITORIAL_PATH).add(ediorialModel).await()
            ResultState.Success("Editorial added successfully")
        }catch (e: Exception){
            ResultState.Error(e.message ?: "Unknown error occurred")

        }

    }


}