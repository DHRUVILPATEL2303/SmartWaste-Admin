package com.example.smartwaste_admin.domain.repo.notificationrepo

import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.FcmModel
import kotlinx.coroutines.flow.Flow

interface NotificationRepositry {


    suspend fun getAllUsersFCMToken() : Flow<ResultState<List<FcmModel>>>

    suspend fun sendNotificationToAllUsers( title: String,
                                            message: String) : Flow<ResultState<String>>

}