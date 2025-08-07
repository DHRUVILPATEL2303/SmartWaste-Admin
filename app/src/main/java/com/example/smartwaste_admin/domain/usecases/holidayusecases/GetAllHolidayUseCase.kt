package com.example.smartwaste_admin.domain.usecases.holidayusecases

import com.example.smartwaste_admin.domain.repo.holidayrepo.HolidayRepositry
import javax.inject.Inject

class GetAllHolidayUseCase @Inject constructor(
    private val holidayRepositry: HolidayRepositry
) {

    suspend fun getAllHolidays() = holidayRepositry.getAllHolidays()
}
