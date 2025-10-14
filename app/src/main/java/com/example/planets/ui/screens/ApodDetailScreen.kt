package com.example.planets.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.planets.R
import com.example.planets.data.model.ApodItem
import com.example.planets.ui.viewmodel.ApodViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodDetailScreen(
    apod: ApodItem,
    onBackClick: () -> Unit,
    viewModel: ApodViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var isFavorite by remember { mutableStateOf(false) }
    
    // Check if item is favorite
    LaunchedEffect(apod.date) {
        isFavorite = viewModel.isFavorite(apod.date)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.toggleFavorite(apod)
                                isFavorite = !isFavorite
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Изображение
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(apod.url)
                    .memoryCacheKey(apod.url)
                    .diskCacheKey(apod.url)
                    .build(),
                contentDescription = apod.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.planet1),
                            contentDescription = "Planet placeholder",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.planet1),
                            contentDescription = "Planet placeholder",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            )
            
            // Заголовок
            Text(
                text = apod.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
            
            // Дата
            Text(
                text = "Дата: ${apod.date}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Тип медиа
            Text(
                text = "Тип: ${apod.mediaType}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Описание
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = apod.explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Justify
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
