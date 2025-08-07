package com.example.smartwaste_admin.domain.repo.reportsrepo

import kotlinx.coroutines.flow.Flow

interface ReportsRepositry {

    suspend fun getAllReports() : Flow<>
}