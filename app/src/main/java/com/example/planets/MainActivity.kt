package com.example.planets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.planets.navigation.ApodNavigation
import com.example.planets.ui.theme.PlanetsTheme
import com.example.planets.ui.viewmodel.ApodViewModel

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
                           val context = LocalContext.current
                           val viewModel: ApodViewModel = viewModel(
                               key = "apod_viewmodel"
                           ) {
                               ApodViewModel(context.applicationContext as android.app.Application)
                           }
                           
                           ApodNavigation(viewModel = viewModel)
                       }
            }
        }
    }
}