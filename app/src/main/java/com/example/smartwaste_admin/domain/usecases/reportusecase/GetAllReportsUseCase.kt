package com.example.smartwaste_admin.domain.usecases.reportusecase

import com.example.smartwaste_admin.domain.repo.reportrepo.ReportRepositry
import javax.inject.Inject

class GetAllReportsUseCase @Inject constructor(
    private val reportRepository: ReportRepositry
) {

    suspend fun getAllReportsUseCase() = reportRepository.getAllReports()
}