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
}