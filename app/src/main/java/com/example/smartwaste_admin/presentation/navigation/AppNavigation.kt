package com.example.smartwaste_admin.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bottombar.AnimatedBottomBar
import com.example.bottombar.components.BottomBarItem
import com.example.bottombar.model.IndicatorDirection
import com.example.bottombar.model.IndicatorStyle
import com.example.smartwaste_admin.presentation.screens.Home.HomeScreenUI
import com.example.smartwaste_admin.presentation.screens.areascreens.AddAreaScreenUI
import com.example.smartwaste_admin.presentation.screens.holidayscreens.AddHolidayScreenUI
import com.example.smartwaste_admin.presentation.screens.holidayscreens.HolidayScreenUI
import com.example.smartwaste_admin.presentation.screens.reportscreens.ReportScreenUI
import com.example.smartwaste_admin.presentation.screens.routesscreen.AddRouteScreenUI
import com.example.smartwaste_admin.presentation.screens.routesscreen.RouteDetailsScreenUI
import com.example.smartwaste_admin.presentation.screens.trucksscreens.AddTruckScreenUI

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentBaseRoute = currentRoute?.substringBefore("?")?.substringBefore("/")

    var selectedItem by remember { mutableIntStateOf(0) }
    val currentIndex = adminBottomBarRoutes.indexOf(currentBaseRoute)
    if (currentIndex != -1) selectedItem = currentIndex

    Scaffold(
        bottomBar = {
            if (currentBaseRoute in adminBottomBarRoutes) {
                AnimatedBottomBar(
                    selectedItem = selectedItem,
                    itemSize = adminBottomBarItems.size,
                    containerColor = Color(0xFF2E3C4A),
                    indicatorStyle = IndicatorStyle.FILLED,
                    containerShape = RoundedCornerShape(50.dp),
                    bottomBarHeight = 65.dp,
                    modifier = Modifier.padding(16.dp).navigationBarsPadding(),
                    indicatorColor = Color.White.copy(alpha = 0.4f),
                    indicatorDirection = IndicatorDirection.BOTTOM,

                )
                {
                    adminBottomBarItems.forEachIndexed { index, item ->
                        BottomBarItem(
                            selected = selectedItem == index,
                            onClick = {
                                if (selectedItem != index) {
                                    selectedItem = index
                                    val route = when (index) {
                                        0 -> Routes.HomeScreen
                                        1 -> Routes.ReportsScreen
                                        2 -> Routes.AssignedTrucksScreen
                                        3 -> Routes.HolidaysScreen
                                        4 -> Routes.NotificationsScreen
                                        5 -> Routes.ExtraServicesScreen
                                        else -> Routes.HomeScreen
                                    }
                                    navController.navigate(route) {
                                        popUpTo(Routes.HomeScreen) {
                                            inclusive = false
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            imageVector = item.icon,
                            iconColor = if (selectedItem == index) Color.Red else Color.White,
                            label = item.name,
                            contentColor = Color.Red,
                            textColor = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        BackHandler(enabled = currentBaseRoute in adminBottomBarRoutes && currentBaseRoute != Routes.HomeScreen::class.qualifiedName) {
            navController.navigate(Routes.HomeScreen) {
                popUpTo(Routes.HomeScreen) { inclusive = false }
                launchSingleTop = true
            }
            selectedItem = 0
        }

        NavHost(
            navController = navController,
            startDestination = Routes.HomeScreen,
            modifier = Modifier

                .fillMaxSize()
        ) {
            composable<Routes.HomeScreen> { HomeScreenUI(navController=navController) }
            composable<Routes.ReportsScreen> { ReportScreenUI(navController=navController) }
            composable<Routes.AssignedTrucksScreen> { AssignedTrucksScreenUI(navController) }
            composable<Routes.HolidaysScreen> { HolidayScreenUI(navController=navController) }
            composable<Routes.NotificationsScreen> { NotificationsScreenUI(navController) }
            composable<Routes.ExtraServicesScreen> { ExtraServicesScreenUI(navController) }

            composable<Routes.AddTruckScreen> { AddTruckScreenUI(navController = navController) }
            composable<Routes.AddAreaScreen> { AddAreaScreenUI(navController = navController) }
            composable<Routes.AddRouteScreen> { AddRouteScreenUI(navController = navController) }
            composable<Routes.AddHolidayScreen> {
                AddHolidayScreenUI(navController=navController)
            }
            composable<Routes.RouteDetailsScren> {
                val data = it.toRoute<Routes.RouteDetailsScren>()
                RouteDetailsScreenUI(routeId = data.routeId, navController = navController)
            }
        }
    }
}



@Composable
fun AssignedTrucksScreenUI(navController: NavHostController) {
    Text("Assigned Trucks Screen")
}


@Composable
fun NotificationsScreenUI(navController: NavHostController) {
    Text("Notifications Screen")
}

@Composable
fun ExtraServicesScreenUI(navController: NavHostController) {
    Text("Extra Services Screen")
}



data class AdminBottomBarItem(
    val name: String,
    val icon: ImageVector
)

val adminBottomBarItems = listOf(
    AdminBottomBarItem("Home", Icons.Default.Home),
    AdminBottomBarItem("Reports", Icons.Default.Star),
    AdminBottomBarItem("Trucks", Icons.Default.LocalShipping),
    AdminBottomBarItem("Holidays", Icons.Default.CalendarToday),
    AdminBottomBarItem("Notifications", Icons.Default.Notifications),
    AdminBottomBarItem("Services", Icons.Default.Build)
)

val adminBottomBarRoutes = listOf(
    Routes.HomeScreen::class.qualifiedName,
    Routes.ReportsScreen::class.qualifiedName,
    Routes.AssignedTrucksScreen::class.qualifiedName,
    Routes.HolidaysScreen::class.qualifiedName,
    Routes.NotificationsScreen::class.qualifiedName,
    Routes.ExtraServicesScreen::class.qualifiedName
)