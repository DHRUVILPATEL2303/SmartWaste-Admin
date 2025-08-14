package com.example.smartwaste_admin.domain.usecases.notificationusecase

import com.example.smartwaste_admin.domain.repo.notificationrepo.NotificationRepositry
import javax.inject.Inject

class SendNotificationToAllUseCase @Inject constructor(
    private val notificationRepositry: NotificationRepositry
) {

    suspend fun sendNotificationToAllUsers( title: String,
                                            message: String) = notificationRepositry.sendNotificationToAllUsers(title,message)
}