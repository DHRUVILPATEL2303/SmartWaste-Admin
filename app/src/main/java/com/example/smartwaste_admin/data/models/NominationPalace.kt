package com.example.smartwaste_admin.data.models

import com.google.gson.annotations.SerializedName


data class NominatimPlace(
    @SerializedName("display_name") val displayName: String,
    @SerializedName("lat") val lat: String,
    @SerializedName("lon") val lon: String
)