package com.example.smartwaste_admin.domain.usecases.workersUseCase

import com.example.smartwaste_admin.domain.repo.workerrepo.WorkersRepositry
import javax.inject.Inject

class GetWorkerByIdUseCase @Inject constructor(
    private val workerRepository: WorkersRepositry
) {
    suspend fun getWorkerByID(id: String) = workerRepository.getWorkerById(
        id = id
    )
}