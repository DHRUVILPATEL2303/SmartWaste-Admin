package com.example.smartwaste_admin.data.models



data class RouteModel(
    val id: String = "",             // Unique ID for the route
    val name: String = "",           // Human-readable name (e.g., "Route A - North Zone")
    val areaList: List<String> = emptyList(), // List of areas/localities this route covers
    val isActive: Boolean = true     // To disable a route if needed
)
