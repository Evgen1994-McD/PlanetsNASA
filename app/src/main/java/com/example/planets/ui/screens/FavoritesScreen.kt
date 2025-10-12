package com.example.planets.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.planets.R
import com.example.planets.data.model.ApodItem
import com.example.planets.ui.viewmodel.ApodViewModel
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    viewModel: ApodViewModel,
    onApodClick: (ApodItem) -> Unit
) {
    val favorites by viewModel.favoritesFlow.collectAsState(initial = emptyList())
    
    if (favorites.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorites",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Избранное",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Здесь будут отображаться ваши избранные изображения",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(favorites) { apod ->
                FavoriteCard(
                    apod = apod, 
                    viewModel = viewModel,
                    onClick = { onApodClick(apod) }
                )
            }
        }
    }
}

@Composable
fun FavoriteCard(
    apod: ApodItem,
    viewModel: ApodViewModel,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Column {
                SubcomposeAsyncImage(
                    model = apod.hdurl ?: apod.url,
                    contentDescription = apod.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { onClick() },
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.planet1),
                                contentDescription = "Planet placeholder",
                                modifier = Modifier.size(48.dp)
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
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                )
                
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = apod.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Дата: ${apod.date}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Тип: ${apod.mediaType}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Favorite button (always red since it's in favorites)
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        viewModel.toggleFavorite(apod)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Удалить из избранного",
                    tint = Color.Red
                )
            }
        }
    }
}
