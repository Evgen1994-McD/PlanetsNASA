package com.example.planets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.planets.domain.usecase.ClearCacheUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val clearCacheUseCase: ClearCacheUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun clearOldCache() {
        viewModelScope.launch {
            try {
                clearCacheUseCase.clearOldCache()
                _uiState.value = _uiState.value.copy(
                    successMessage = "Старый кэш очищен",
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка при очистке кэша: ${e.message}",
                    successMessage = null
                )
            }
        }
    }

    fun clearAllCache() {
        viewModelScope.launch {
            try {
                clearCacheUseCase()
                _uiState.value = _uiState.value.copy(
                    successMessage = "Весь кэш очищен",
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка при очистке кэша: ${e.message}",
                    successMessage = null
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            error = null
        )
    }
}

data class SettingsUiState(
    val successMessage: String? = null,
    val error: String? = null
)
