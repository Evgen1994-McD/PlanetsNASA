package com.example.planets.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.planets.R
import com.example.planets.domain.model.Apod
import com.example.planets.presentation.components.GeneralErrorScreen
import com.example.planets.presentation.components.HttpErrorScreen
import com.example.planets.presentation.components.NetworkErrorScreen
import com.example.planets.presentation.viewmodel.ApodListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodListScreen(
    viewModel: ApodListViewModel,
    onApodClick: (Apod) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val refreshTrigger by viewModel.refreshTrigger.collectAsState()

    val apodPagingItems = viewModel.apodPagingFlow.collectAsLazyPagingItems()
    
    // Сохраняем состояние списка между навигацией
    val listState = rememberSaveable(
        saver = LazyGridState.Saver
    ) {
        LazyGridState()
    }

    // Обновляем данные при очистке кэша
    LaunchedEffect(refreshTrigger) {
        if (refreshTrigger > 0) {
            apodPagingItems.refresh()
        }
    }
    
    // Обработка ошибок загрузки
    LaunchedEffect(apodPagingItems.loadState.refresh) {
        when (val refreshState = apodPagingItems.loadState.refresh) {
            is LoadState.Error -> {
                val error = refreshState.error
                viewModel.setRetrying(false) // Сбрасываем состояние повторной попытки при ошибке
                when {
                    error.message?.contains("No internet connection and no cached data available") == true -> {
                        viewModel.setNetworkError()
                    }
                    error.message?.contains("HTTP 404") == true -> {
                        viewModel.setHttpError(404)
                    }
                    error.message?.contains("HTTP 5") == true -> {
                        viewModel.setHttpError(500)
                    }
                    error.message?.contains("Unable to resolve host") == true -> {
                        viewModel.setNetworkError()
                    }
                    else -> {
                        viewModel.setHttpError(0)
                    }
                }
            }
            is LoadState.Loading -> {
                viewModel.setLoading(true)
            }
            is LoadState.NotLoading -> {
                viewModel.setLoading(false)
                viewModel.setRetrying(false)
                viewModel.clearError()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("NASA APOD")
                        if (apodPagingItems.loadState.refresh is LoadState.Error) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Офлайн режим",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { apodPagingItems.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Обновить"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // Показываем экраны ошибок только если нет данных из кэша
                uiState.hasNetworkError && apodPagingItems.itemCount == 0 -> {
                    NetworkErrorScreen(
                        onRetry = { 
                            viewModel.setRetrying(true)
                            apodPagingItems.retry()
                        },
                        isRetrying = uiState.isRetrying
                    )
                }
                
                uiState.hasHttpError && apodPagingItems.itemCount == 0 -> {
                    HttpErrorScreen(
                        errorCode = uiState.httpErrorCode ?: 0,
                        onRetry = { 
                            viewModel.setRetrying(true)
                            apodPagingItems.retry()
                        },
                        isRetrying = uiState.isRetrying
                    )
                }
                
                apodPagingItems.loadState.refresh is LoadState.Loading && apodPagingItems.itemCount == 0 -> {
                    // Показываем прогресс только при первой загрузке
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                apodPagingItems.loadState.refresh is LoadState.Error && apodPagingItems.itemCount == 0 -> {
                    val error = apodPagingItems.loadState.refresh as LoadState.Error
                    GeneralErrorScreen(
                        message = error.error.message ?: "Неизвестная ошибка",
                        onRetry = { 
                            viewModel.setRetrying(true)
                            apodPagingItems.retry()
                        },
                        isRetrying = uiState.isRetrying
                    )
                }
                
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = listState,
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            count = apodPagingItems.itemCount,
                            key = { index -> "apod_$index" },
                            contentType = { "apod" }
                        ) { index ->
                            val apod = apodPagingItems[index]
                            if (apod != null) {
                                ApodCard(
                                    apod = apod,
                                    onClick = { onApodClick(apod) },
                                    viewModel = viewModel
                                )
                            } else {
                                // Placeholder while loading
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                        
                        // Append loading state - показываем только если есть данные
                        if (apodPagingItems.loadState.append is LoadState.Loading && apodPagingItems.itemCount > 0) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        
                        // Append error state
                        if (apodPagingItems.loadState.append is LoadState.Error) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Ошибка загрузки",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                        
                        // Показываем индикатор офлайн режима, если есть данные из кэша и есть ошибка
                        if (apodPagingItems.itemCount > 0 && 
                            (apodPagingItems.loadState.refresh is LoadState.Error || 
                             apodPagingItems.loadState.append is LoadState.Error)) {
                            item(span = { GridItemSpan(2) }) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Офлайн режим",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "Показываются сохранённые данные. Проверьте подключение к интернету.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApodCard(
    apod: Apod,
    onClick: () -> Unit,
    viewModel: ApodListViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var isFavorite by remember { mutableStateOf(false) }
    
    // Check if item is favorite
    LaunchedEffect(apod.date) {
        isFavorite = viewModel.isFavorite(apod.date)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(apod.url)
                        .memoryCacheKey(apod.url)
                        .diskCacheKey(apod.url)
                        .build(),
                    contentDescription = apod.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
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
                
                Text(
                    text = apod.title,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp),
                    maxLines = 2
                )
                
                Text(
                    text = apod.date,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Favorite button
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        viewModel.toggleFavorite(apod)
                        isFavorite = !isFavorite
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
            }
        }
    }
}
