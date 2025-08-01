package com.example.smartwaste_admin.data.models


data class RouteProgressModel(
    val truckId: String = "",
    val routeId: String = "",
    val completedAreas: List<String> = emptyList(),
    val date: String = "",
)
