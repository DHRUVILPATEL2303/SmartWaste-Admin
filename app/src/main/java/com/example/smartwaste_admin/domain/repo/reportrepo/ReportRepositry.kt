package com.example.smartwaste_admin.domain.repo.reportrepo

import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.ReportModel
import kotlinx.coroutines.flow.Flow

interface ReportRepositry {

    suspend fun getAllReports() : Flow<ResultState<List<ReportModel>>>

    suspend fun deleteReport(reportID:String) : Flow<ResultState<String>>
}