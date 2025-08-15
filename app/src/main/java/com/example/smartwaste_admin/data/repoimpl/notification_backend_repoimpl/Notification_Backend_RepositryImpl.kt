//package com.example.smartwaste_admin.data.repoimpl.notification_backend_repoimpl
//
//import com.example.smartwaste_admin.domain.repo.notification_backend_repo.NotificationApi
//import com.example.smartwaste_admin.domain.repo.notification_backend_repo.NotificationBackendRepositry
//import com.example.smartwaste_admin.domain.repo.notification_backend_repo.NotificationRequest
//import retrofit2.Response
//import javax.inject.Inject
//
//class Notification_Backend_RepositryImpl @Inject constructor(
//    private val api: NotificationApi
//) : NotificationBackendRepositry {
//
//    override suspend fun sendNotification(
//        token: String,
//        title: String,
//        message: String
//    ): Response<String> {
//        return try {
//            val request = NotificationRequest(token, title, message)
//            val response = api.sendNotification(request)
//            if (response.isSuccessful && response.body()?.success == true) {
//                Response.success("Notification sent successfully")
//            } else {
//                Response.error(
//                    response.code(),
//                    response.errorBody() ?: okhttp3.ResponseBody.create(null, "Unknown error")
//                )
//            }
//        } catch (e: Exception) {
//            Response.error(500, okhttp3.ResponseBody.create(null, "Exception: ${e.message}"))
//        }
//    }
//}