package com.example.smartwaste_admin.data.remote

import com.example.smartwaste_admin.data.models.NominatimPlace
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {
    @GET("search")
    suspend fun searchPlace(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("limit") limit: Int = 5
    ): List<NominatimPlace>
}


object RetrofitInstance {
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "SmartWasteApp/1.0 (pateldhruvil2021@example.com)")

                .build()
            chain.proceed(request)
        }
        .build()

    val api: NominatimApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/") // OSM Nominatim
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NominatimApi::class.java)
    }
}