package com.example.smartwaste_admin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartwaste_admin.presentation.screens.Home.HomeScreenUI

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
    }

}