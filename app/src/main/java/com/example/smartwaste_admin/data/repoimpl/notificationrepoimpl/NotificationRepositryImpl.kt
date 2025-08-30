package com.example.smartwaste_admin.data.repoimpl.notificationrepoimpl

import android.content.Context
import android.util.Log
import androidx.collection.emptyIntSet
import com.example.smartwaste_admin.R
import com.example.smartwaste_admin.common.FCM_PATH
import com.example.smartwaste_admin.common.ResultState
import com.example.smartwaste_admin.data.models.FcmModel
import com.example.smartwaste_admin.domain.repo.notification_backend_repo.NotificationApi
import com.example.smartwaste_admin.domain.repo.notification_backend_repo.NotificationRequest
import com.example.smartwaste_admin.domain.repo.notificationrepo.NotificationRepositry
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.InputStream
import javax.inject.Inject

class NotificationRepositryImpl @Inject constructor(
    private val context: Context,
    private val firebaseFirestore: FirebaseFirestore,
    private val api: NotificationApi
) : NotificationRepositry {

    private val client = OkHttpClient()
    private var accessToken = ""
    private val fcmUrl = "https://fcm.googleapis.com/v1/projects/smartwaste-user/messages:send"

    override suspend fun getAllUsersFCMToken(): Flow<ResultState<List<FcmModel>>> = flow {
        emit(ResultState.Loading)
        try {
            val snapshot = firebaseFirestore.collection(FCM_PATH).get().await()
            val tokens = snapshot.documents.mapNotNull { it.toObject(FcmModel::class.java) }
            emit(ResultState.Success(tokens))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun sendNotificationToAllUsers(
        title: String,
        message: String
    ): kotlinx.coroutines.flow.Flow<ResultState<String>> {
        return kotlinx.coroutines.flow.flow {
            try {
                getAccessToken()
                val tokens = getAllTokens()
                Log.d("NotificationRepo", "Tokens: $tokens")
                tokens.forEach { token ->
                    sendNotification(token, title, message)

                }
                emit(ResultState.Success("Notifications sent successfully"))
            } catch (e: Exception) {
                Log.e("NotificationRepo", "Error sending notifications: ${e.message}", e)
                emit(ResultState.Error(e.message.toString()))
            }
        }
    }

//    suspend fun sendNotificationToUser(
//        userId: String,
//        title: String,
//        message: String
//    ): ResultState<String> {
//        return try {
//            getAccessToken()
//            val token = getUserFCM(userId)
//            return if (token != null) {
//                sendNotification(token, title, message)
//                ResultState.Success("Notification sent to user $userId")
//            } else {
//                ResultState.Error("FCM token not found for user $userId")
//            }
//        } catch (e: Exception) {
//            Log.e("NotificationRepo", "Error sending notification to user: ${e.message}", e)
//            ResultState.Error(e.message.toString())
//        }
//    }

    private suspend fun getUserFCM(userId: String): String? {
        return try {
            val snapshot = firebaseFirestore.collection(FCM_PATH).document(userId).get().await()
            snapshot.getString("fcm_token")
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error fetching FCM token: ${e.message}", e)
            null
        }
    }

    private suspend fun getAccessToken() {
        withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream = context.resources.openRawResource(R.raw.apikey)
                val credentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped("https://www.googleapis.com/auth/firebase.messaging")
                credentials.refresh()
                accessToken = credentials.accessToken.tokenValue
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("NotificationRepo", "getAccessToken error: ${e.message}")
            }
        }
    }

    private suspend fun getAllTokens(): List<String> {
        return withContext(Dispatchers.IO) {
            val snapshot = firebaseFirestore.collection(FCM_PATH).get().await()
            snapshot.documents.mapNotNull { it.getString("fcm_token") }


        }
    }

    private suspend fun sendNotification(token: String, title: String, message: String) {
        val json = JSONObject().apply {
            put("message", JSONObject().apply {
                put("token", token)
                put("notification", JSONObject().apply {
                    put("title", title)
                    put("body", message)

                })
                put("data", JSONObject().apply {
                    put("title", title)
                    put("body", message)

                })
                put("android", JSONObject().apply {
                    put("priority", "high")
                    put("notification", JSONObject().apply {
                        put("icon", "ic_launcher_foreground")
                        put("color", "#FF0000")
                        put("sound", "default")

                    })
                })
            })
        }

        val requestBody = json.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(fcmUrl)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d(
                    "NotificationRepo",
                    "Notification sent successfully: ${response.body?.string()}"
                )
            } else {
                Log.e("NotificationRepo", "Notification error: ${response.body?.string()}")
            }
        }
    }
}