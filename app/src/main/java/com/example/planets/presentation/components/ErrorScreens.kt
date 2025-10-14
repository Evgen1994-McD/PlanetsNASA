package com.example.planets.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun NetworkErrorScreen(
    onRetry: () -> Unit,
    isRetrying: Boolean = false,
    modifier: Modifier = Modifier
) {
    ErrorScreen(
        icon = Icons.Default.Warning,
        title = "Нет подключения к интернету",
        message = "Проверьте подключение к сети и попробуйте снова",
        onRetry = onRetry,
        isRetrying = isRetrying,
        modifier = modifier
    )
}

@Composable
fun HttpErrorScreen(
    errorCode: Int,
    onRetry: () -> Unit,
    isRetrying: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (title, message) = when (errorCode) {
        404 -> "Страница не найдена" to "Запрашиваемые данные не найдены на сервере"
        500 -> "Ошибка сервера" to "Внутренняя ошибка сервера. Попробуйте позже"
        503 -> "Сервис недоступен" to "Сервис временно недоступен"
        else -> "Ошибка загрузки" to "Произошла ошибка при загрузке данных (код: $errorCode)"
    }
    
    ErrorScreen(
        icon = Icons.Default.Warning,
        title = title,
        message = message,
        onRetry = onRetry,
        isRetrying = isRetrying,
        modifier = modifier
    )
}

@Composable
fun GeneralErrorScreen(
    message: String,
    onRetry: () -> Unit,
    isRetrying: Boolean = false,
    modifier: Modifier = Modifier
) {
    ErrorScreen(
        icon = Icons.Default.Warning,
        title = "Произошла ошибка",
        message = message,
        onRetry = onRetry,
        isRetrying = isRetrying,
        modifier = modifier
    )
}

@Composable
private fun ErrorScreen(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String,
    onRetry: () -> Unit,
    isRetrying: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRetry,
            enabled = !isRetrying,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isRetrying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Загрузка...")
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Попробовать снова")
            }
        }
    }
}
