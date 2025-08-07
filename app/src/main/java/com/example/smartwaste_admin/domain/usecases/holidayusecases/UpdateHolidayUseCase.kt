package com.example.smartwaste_admin.domain.usecases.holidayusecases

import com.example.smartwaste_admin.data.models.HolidayModel
import com.example.smartwaste_admin.domain.repo.holidayrepo.HolidayRepositry
import javax.inject.Inject

class UpdateHolidayUseCase @Inject constructor(
    private val holidayRepositry: HolidayRepositry
) {
    suspend fun updateHoliday(holidayModel: HolidayModel)=holidayRepositry.updateHoliday(holidayModel)
}