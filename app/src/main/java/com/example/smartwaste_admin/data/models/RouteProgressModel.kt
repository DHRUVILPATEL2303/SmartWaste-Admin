package com.example.smartwaste_admin.data.models


data class RouteProgressModel(
    val truckId: String = "",
    val routeId: String = "",
    val completedAreas: List<AreaMap> = emptyList(),
    val date: String = "",

)

data class AreaMap(

val areadId:String="",
val areaName:String="",
val areaCompleted : Boolean = false
)
