package com.example.planets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.planets.navigation.ApodNavigation
import com.example.planets.presentation.components.BottomNavigationBar
import com.example.planets.presentation.theme.PlanetsTheme
import com.example.planets.presentation.viewmodel.ApodListViewModel
import com.example.planets.presentation.viewmodel.ApodDetailViewModel
import com.example.planets.presentation.viewmodel.FavoritesViewModel
import com.example.planets.presentation.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanetsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val apodListViewModel: ApodListViewModel = viewModel()
    val apodDetailViewModel: ApodDetailViewModel = viewModel()
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    // Получаем текущий маршрут для определения, показывать ли Bottom Menu
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Скрываем Bottom Menu на детальном экране
    val showBottomBar = currentRoute != "apod_detail"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        ApodNavigation(
            navController = navController,
            apodListViewModel = apodListViewModel,
            apodDetailViewModel = apodDetailViewModel,
            favoritesViewModel = favoritesViewModel,
            settingsViewModel = settingsViewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}