package com.example.smartwaste_admin.presentation.viewmodels.reportsviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.ReportModel
import com.example.smartwaste_admin.domain.usecases.reportusecase.DeleteReportUseCase
import com.example.smartwaste_admin.domain.usecases.reportusecase.GetAllReportsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ReportViewModel @Inject constructor(
    private val getAllReportsUseCase: GetAllReportsUseCase,
    private val deleteReportUseCase: DeleteReportUseCase
) : ViewModel() {

    private val _reportState = MutableStateFlow(CommonReportState<List<ReportModel>>())
    val reportState = _reportState.asStateFlow()

    private val _deleteReportState = MutableStateFlow(CommonReportState<String>())
    val deleteReportState = _deleteReportState.asStateFlow()


    fun getAllReports() {
        viewModelScope.launch {
            getAllReportsUseCase.getAllReportsUseCase().collect {

                when (it) {
                    is ResultState.Loading -> {
                        _reportState.value = CommonReportState(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _reportState.value = CommonReportState(success = it.data)
                    }

                    is ResultState.Error -> {
                        _reportState.value = CommonReportState(error = it.error)
                    }


                }
            }


        }
    }

    fun deleteREport(reportID: String){
        viewModelScope.launch {
            deleteReportUseCase.deleteReport(reportID).collect{
                when(it){


                    is ResultState.Loading -> {
                        _deleteReportState.value = CommonReportState(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _deleteReportState.value = CommonReportState(success = it.data)
                    }


                    is ResultState.Error -> {
                        _deleteReportState.value = CommonReportState(error = it.error)
                    }


                }
            }
        }
    }

}

data class CommonReportState<T>(
    val isLoading: Boolean = false,
    val success: T? = null,
    val error: String = ""
)