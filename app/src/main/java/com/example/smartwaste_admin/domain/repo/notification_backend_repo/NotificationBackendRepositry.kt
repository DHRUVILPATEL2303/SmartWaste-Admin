package com.example.smartwaste_admin.domain.repo.notification_backend_repo

import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


data class NotificationRequest(
    val token: String,
    val title: String,
    val message: String
)

data class NotificationResponse(

    val message: String
)



interface NotificationApi {
    @POST("api/notify")
    suspend fun sendNotification(
        @Query("token") token: String,
        @Query("title") title: String,
        @Query("body") body: String
    ): Response<String>


    @POST("api/notify/all")
    suspend fun sendNotificationToAll(
        @Query("title") title: String,
        @Query("body") body: String
    ): Response<String>
}

object RetrofitInstance {

    private const val BASE_URL = "https://notificatoin-smart-waste.onrender.com/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)   // ⬅ increase timeout
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: NotificationApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotificationApi::class.java)
    }
}