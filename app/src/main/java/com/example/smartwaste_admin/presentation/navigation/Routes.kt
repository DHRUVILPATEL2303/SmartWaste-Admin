package com.example.smartwaste_admin.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    object HomeScreen

    @Serializable
    object NotificationScreen

    @Serializable
    object AddRouteScreen

    @Serializable
    object AddTruckScreen

    @Serializable
    object ReportsScreen

    @Serializable
    object AllWorkerScreen

    @Serializable
    object AllResidentScreen

    @Serializable
    object AddAreaScreen


    @Serializable
    data class RouteDetailsScren(
        val routeId: String
    )


    @Serializable
    object AssignedTrucksScreen

    @Serializable
    object HolidaysScreen

    @Serializable
    object NotificationsScreen

    @Serializable
    object ExtraServicesScreen

    @Serializable
    object AddHolidayScreen

    @Serializable
    object  AddEditorialScreen
}