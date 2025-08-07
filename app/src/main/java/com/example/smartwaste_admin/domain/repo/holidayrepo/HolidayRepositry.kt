package com.example.smartwaste_admin.domain.repo.holidayrepo

import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.HolidayModel
import kotlinx.coroutines.flow.Flow

interface HolidayRepositry {

    suspend fun getAllHolidays(): Flow<ResultState<List<HolidayModel>>>

    suspend fun addHoliday(holidayModel: HolidayModel) : ResultState<String>

    suspend fun updateHoliday(holidayModel: HolidayModel) : ResultState<String>
}