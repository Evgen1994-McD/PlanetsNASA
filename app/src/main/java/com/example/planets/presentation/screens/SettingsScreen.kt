package com.example.planets.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.planets.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showClearCacheDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Настройки",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Кнопка очистки кэша
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Очистить кэш",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Удалить все сохранённые APOD из кэша и избранного",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Button(
                    onClick = { showClearCacheDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear cache",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Очистить")
                }
            }
        }
    }
    
    // Диалог подтверждения
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = {
                Text("Очистить кэш?")
            },
            text = {
                Text("Это действие удалит все сохранённые APOD из кэша и избранного. При следующей загрузке данные будут загружены заново.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllCache()
                        showClearCacheDialog = false
                    }
                ) {
                    Text("Очистить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearCacheDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
    
    // Диалог успешной очистки или ошибки
    if (uiState.successMessage != null || uiState.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessages() },
            title = {
                Text(if (uiState.successMessage != null) "Успешно" else "Ошибка")
            },
            text = {
                Text(uiState.successMessage ?: uiState.error ?: "")
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearMessages() }
                ) {
                    Text("ОК")
                }
            }
        )
    }
}
