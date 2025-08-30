package com.example.smartwaste_admin.domain.repo.editorialrepo

import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.EdiorialModel

interface EditorialRepositry {

    suspend fun addEditorial(ediorialModel: EdiorialModel) : ResultState<String>
}