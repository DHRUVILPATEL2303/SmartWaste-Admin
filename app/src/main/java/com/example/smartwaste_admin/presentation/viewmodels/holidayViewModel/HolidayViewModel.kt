package com.example.smartwaste_admin.presentation.viewmodels.holidayViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.HolidayModel
import com.example.smartwaste_admin.domain.usecases.holidayusecases.AddHolidayUseCase
import com.example.smartwaste_admin.domain.usecases.holidayusecases.GetAllHolidayUseCase
import com.example.smartwaste_admin.domain.usecases.holidayusecases.UpdateHolidayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HolidayViewModel @Inject constructor(
    private val getAllHolidayUseCase: GetAllHolidayUseCase,
    private val addHolidayUseCase: AddHolidayUseCase,
    private val updateHolidayUseCase: UpdateHolidayUseCase
): ViewModel(){

    private val _allHolidayState= MutableStateFlow(CommonHolidayState<List<HolidayModel>>())
    val allHolidayState=_allHolidayState.asStateFlow()


    private val _addHolidayState= MutableStateFlow(CommonHolidayState<String>())
    val addHolidayState=_addHolidayState.asStateFlow()


    private val _updateHolidayState= MutableStateFlow(CommonHolidayState<String>())
    val updateHolidayState=_updateHolidayState.asStateFlow()


    fun getAllHoliday(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllHolidayUseCase.getAllHolidays().collect{

                when(it){
                    is ResultState.Loading->{
                        _allHolidayState.value=CommonHolidayState(isLoading = true)
                    }
                    is ResultState.Success->{
                        _allHolidayState.value=CommonHolidayState(succcess = it.data, isLoading = false)
                    }

                    is ResultState.Error->{
                        _allHolidayState.value=CommonHolidayState(error = it.error, isLoading = false)
                    }


                }
            }

        }
    }

    fun addHoliday(holidayModel: HolidayModel){

        viewModelScope.launch(Dispatchers.IO)  {

            when(val result=addHolidayUseCase.addHolidayUseCase(holidayModel)){
                is ResultState.Loading->{
                    _addHolidayState.value=CommonHolidayState(isLoading = true)
                }

                is ResultState.Success->{
                    _addHolidayState.value=CommonHolidayState(succcess = result.data, isLoading = false)
                }
                is ResultState.Error->{
                    _addHolidayState.value=CommonHolidayState(error = result.error, isLoading = false)
                }
            }


        }
    }

    fun updateHoliday(holidayModel: HolidayModel){

        viewModelScope.launch(Dispatchers.IO) {

            when(val result=updateHolidayUseCase.updateHoliday(holidayModel)){
                is ResultState.Loading->{
                    _updateHolidayState.value=CommonHolidayState(isLoading = true)
                }
                is ResultState.Success->{
                    _updateHolidayState.value=CommonHolidayState(succcess = result.data, isLoading = false)
                }

                is ResultState.Error->{
                    _updateHolidayState.value=CommonHolidayState(error = result.error, isLoading = false)
                }

            }
        }
    }

}


data class CommonHolidayState<T>(
    val isLoading: Boolean = false,
    val succcess: T? = null,
    val error: String = ""
)