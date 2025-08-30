package com.example.smartwaste_admin.domain.usecases.editorialusecase

import com.example.smartwaste_admin.data.models.EdiorialModel
import com.example.smartwaste_admin.domain.repo.editorialrepo.EditorialRepositry
import javax.inject.Inject

class AddEdiroailUseCase @Inject constructor(
    private val editorialRepositry: EditorialRepositry
) {
    suspend fun addEditorial(ediorialModel: EdiorialModel) = editorialRepositry.addEditorial(ediorialModel)
}