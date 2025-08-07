package com.example.smartwaste_admin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.smartwaste_admin.presentation.screens.Home.HomeScreenUI
import com.example.smartwaste_admin.presentation.screens.areascreens.AddAreaScreenUI
import com.example.smartwaste_admin.presentation.screens.routesscreen.AddRouteScreenUI
import com.example.smartwaste_admin.presentation.screens.routesscreen.RouteDetailsScreenUI
import com.example.smartwaste_admin.presentation.screens.trucksscreens.AddTruckScreenUI

@Composable
fun AppNavigation() {


    val navController = rememberNavController()

    NavHost(
        navController=navController,
        startDestination = Routes.HomeScreen
    ){
        composable<Routes.HomeScreen> {
            HomeScreenUI(navController=navController)
        }
        composable<Routes.AddAreaScreen> {
            AddAreaScreenUI(navController=navController)
        }
        composable<Routes.AddRouteScreen> {
            AddRouteScreenUI(navController=navController)
        }
        composable<Routes.RouteDetailsScren> {
            val data=it.toRoute<Routes.RouteDetailsScren>()
            RouteDetailsScreenUI(routeId =data.routeId ,navController=navController)
        }
        composable<Routes.AddTruckScreen> {
            AddTruckScreenUI(navController=navController)
        }
    }

}