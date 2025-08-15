package com.example.smartwaste_admin.domain.repo.notification_backend_repo

import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query



data class NotificationRequest(
    val token: String,
    val title: String,
    val message: String
)

data class NotificationResponse(

    val message: String
)



interface NotificationApi {
    @POST("notify")
    suspend fun sendNotification(
        @Query("token") token: String,
        @Query("title") title: String,
        @Query("body") body: String
    ): Response<String>
}



object RetrofitInstance {

    private const val BASE_URL = "https://notificatoin-smart-waste.onrender.com/api/"



    private val client = OkHttpClient.Builder()
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

