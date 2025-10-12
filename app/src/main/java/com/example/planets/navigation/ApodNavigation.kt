package com.example.planets.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planets.data.model.ApodItem
import com.example.planets.ui.screens.ApodDetailScreen
import com.example.planets.ui.screens.ApodListScreen
import com.example.planets.ui.viewmodel.ApodViewModel

@Composable
fun ApodNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: ApodViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "apod_list"
    ) {
        composable("apod_list") {
            ApodListScreen(
                viewModel = viewModel,
                onApodClick = { apod ->
                    viewModel.selectApod(apod)
                    navController.navigate("apod_detail")
                }
            )
        }
        
        composable("apod_detail") {
            val selectedApod = viewModel.uiState.value.selectedApod
            if (selectedApod != null) {
                ApodDetailScreen(
                    apod = selectedApod,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
