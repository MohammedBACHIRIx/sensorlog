package com.example.sensorlogger.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun SensorApp() {
    val navController = rememberNavController()
    val viewModel: SensorViewModel = viewModel()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onSensorClick = { sensorType ->
                    navController.navigate("detail/$sensorType")
                }
            )
        }
        composable(
            route = "detail/{sensorType}",
            arguments = listOf(navArgument("sensorType") { type = NavType.IntType })
        ) { backStackEntry ->
            val sensorType = backStackEntry.arguments?.getInt("sensorType") ?: return@composable
            DetailScreen(
                viewModel = viewModel,
                sensorType = sensorType,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
