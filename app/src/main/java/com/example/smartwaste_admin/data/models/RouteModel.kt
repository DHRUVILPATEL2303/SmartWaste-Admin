package com.example.smartwaste_admin.data.models

data class AreaInfo(
    val areaId: String = "",
    val areaName: String = "",
    val latitude : Double = 0.0,
    val longitude : Double = 0.0
)
data class RouteModel(
    val id: String = "",
    val name: String = "",
    val areaList: List<AreaInfo> = emptyList(),
    val isActive: Boolean = true
)