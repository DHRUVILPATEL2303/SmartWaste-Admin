package com.example.smartwaste_admin.presentation.viewmodels.notificationviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.domain.usecases.notificationusecase.SendNotificationToAllUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val sendNotificationToAllUseCase: SendNotificationToAllUseCase
) : ViewModel(){

    private val _notificationState = MutableStateFlow(CommonNotificationState<String>())
    val notificationState = _notificationState.asStateFlow()

    fun sendNotificationToAllUsers(title: String, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sendNotificationToAllUseCase.sendNotificationToAllUsers(title,message).collect {
                when(it){
                    is ResultState.Loading -> {
                        _notificationState.value = CommonNotificationState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _notificationState.value = CommonNotificationState(success = it.data, isLoading = false)
                    }
                    is ResultState.Error -> {
                        _notificationState.value = CommonNotificationState(error = it.error, isLoading = false)
                    }
                }
            }
        }
    }


    fun resetState() {
        _notificationState.value = CommonNotificationState()
    }
}

data class CommonNotificationState<T>(
    val isLoading: Boolean = false,
    val success: T? = null,
    val error : String = ""
)