package com.example.planets.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            route = "see",
            title = "Просмотр",
            icon = Icons.Default.Notifications
        ),
        BottomNavItem(
            route = "favorites",
            title = "Избранное",
            icon = Icons.Default.Favorite
        ),
        BottomNavItem(
            route = "settings",
            title = "Настройки",
            icon = Icons.Default.Settings
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Очищаем весь стек навигации при переходе между экранами Bottom Menu
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                            saveState = true  // Сохраняем состояние экранов
                        }
                        // Избегаем множественных копий одного экрана
                        launchSingleTop = true
                        // Восстанавливаем состояние при повторном выборе экрана
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)
