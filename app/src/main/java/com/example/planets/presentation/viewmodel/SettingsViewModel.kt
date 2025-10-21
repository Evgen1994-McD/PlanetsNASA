package com.example.planets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

import com.example.planets.domain.usecase.ClearCacheUseCase
import com.example.planets.domain.usecase.ThemeUseCase
import com.example.planets.domain.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val clearCacheUseCase: ClearCacheUseCase,
    private val themeUseCase: ThemeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    val themeMode: StateFlow<ThemeMode> = themeUseCase.getThemeMode().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeMode.SYSTEM
    )



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
    
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themeUseCase.setThemeMode(mode)
        }
    }
}

data class SettingsUiState(
    val successMessage: String? = null,
    val error: String? = null
)
