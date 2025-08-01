package com.example.smartwaste_admin.data.models

data class TruckModel(
    val id: String = "",
    val truckNumber : String="",
    val area: String = "",
    val date: String = "",
    val driverId: String = "",
    val collectorId: String = "",
    val routeId: String = "", // Reference to RouteModel.id
    val status: String = "pending",
    val timestamp: Long = System.currentTimeMillis()
)
