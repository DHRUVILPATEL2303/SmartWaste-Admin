package com.example.smartwaste_admin.domain.usecases.reportusecase

import com.example.smartwaste_admin.domain.repo.reportrepo.ReportRepositry
import javax.inject.Inject

class DeleteReportUseCase @Inject constructor(
    private val reportRepository: ReportRepositry
) {

    suspend fun deleteReport(reportID: String) = reportRepository.deleteReport(reportID)
}