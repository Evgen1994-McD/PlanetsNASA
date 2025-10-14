package com.example.planets.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planets.data.model.ApodItem
import com.example.planets.ui.screens.ApodDetailScreen
import com.example.planets.ui.screens.ApodListScreen
import com.example.planets.ui.screens.FavoritesScreen
import com.example.planets.ui.screens.SettingsScreen
import com.example.planets.ui.viewmodel.ApodViewModel

@Composable
fun ApodNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: ApodViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "see",
        modifier = modifier
    ) {
        // Main screen with APOD list
        composable("see") {
            ApodListScreen(
                viewModel = viewModel,
                onApodClick = { apod ->
                    viewModel.selectApod(apod)
                    navController.navigate("apod_detail") {
                        // Не сохраняем детальный экран в стеке
                        launchSingleTop = true
                    }
                }
            )
        }
        
        // APOD detail screen
        composable("apod_detail") {
            val uiState = viewModel.uiState.collectAsState()
            val selectedApod = uiState.value.selectedApod
            if (selectedApod != null) {
                ApodDetailScreen(
                    apod = selectedApod,
                    onBackClick = {
                        viewModel.clearSelectedApod()
                        navController.popBackStack()
                    },
                    viewModel = viewModel
                )
            } else {
                // Если нет выбранного APOD, возвращаемся на главный экран
                navController.navigate("see") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
        
        // Favorites screen
        composable("favorites") {
            FavoritesScreen(
                viewModel = viewModel,
                onApodClick = { apod ->
                    viewModel.selectApod(apod)
                    navController.navigate("apod_detail") {
                        // Не сохраняем детальный экран в стеке
                        launchSingleTop = true
                    }
                }
            )
        }
        
        // Settings screen
        composable("settings") {
            SettingsScreen(viewModel = viewModel)
        }
    }
}
