package com.example.smartwaste_admin.data.models

data class TruckModel(
    val id: String = "",
    val truckNumber : String="",
    val timestamp: Long = System.currentTimeMillis()
)
