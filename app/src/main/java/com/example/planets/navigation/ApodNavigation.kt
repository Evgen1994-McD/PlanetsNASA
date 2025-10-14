package com.example.planets.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.planets.presentation.screens.ApodDetailScreen
import com.example.planets.presentation.screens.ApodListScreen
import com.example.planets.presentation.screens.FavoritesScreen
import com.example.planets.presentation.screens.SettingsScreen
import com.example.planets.presentation.viewmodel.ApodListViewModel
import com.example.planets.presentation.viewmodel.ApodDetailViewModel
import com.example.planets.presentation.viewmodel.FavoritesViewModel
import com.example.planets.presentation.viewmodel.SettingsViewModel

@Composable
fun ApodNavigation(
    navController: NavHostController = rememberNavController(),
    apodListViewModel: ApodListViewModel,
    apodDetailViewModel: ApodDetailViewModel,
    favoritesViewModel: FavoritesViewModel,
    settingsViewModel: SettingsViewModel,
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
                        viewModel = apodListViewModel,
                        onApodClick = { apod ->
                            apodDetailViewModel.selectApod(apod)
                            navController.navigate("apod_detail") {
                                // Не сохраняем детальный экран в стеке
                                launchSingleTop = true
                            }
                        }
                    )
                }
        
                // APOD detail screen
                composable("apod_detail") {
                    val uiState = apodDetailViewModel.uiState.collectAsState()
                    val selectedApod = uiState.value.selectedApod
                    if (selectedApod != null) {
                        ApodDetailScreen(
                            apod = selectedApod,
                            onBackClick = {
                                apodDetailViewModel.clearSelectedApod()
                                navController.popBackStack()
                            },
                            viewModel = apodDetailViewModel
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
                        viewModel = favoritesViewModel,
                        onApodClick = { apod ->
                            apodDetailViewModel.selectApod(apod)
                            navController.navigate("apod_detail") {
                                // Не сохраняем детальный экран в стеке
                                launchSingleTop = true
                            }
                        }
                    )
                }

                // Settings screen
                composable("settings") {
                    SettingsScreen(viewModel = settingsViewModel)
                }
    }
}
