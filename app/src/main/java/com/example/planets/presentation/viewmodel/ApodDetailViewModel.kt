package com.example.planets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planets.domain.model.Apod
import com.example.planets.domain.usecase.IsFavoriteUseCase
import com.example.planets.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ApodDetailViewModel @Inject constructor(
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApodDetailUiState())
    val uiState: StateFlow<ApodDetailUiState> = _uiState.asStateFlow()

    fun selectApod(apod: Apod) {
        _uiState.value = _uiState.value.copy(selectedApod = apod)
    }

    fun clearSelectedApod() {
        _uiState.value = _uiState.value.copy(selectedApod = null)
    }

    fun toggleFavorite(apod: Apod) {
        viewModelScope.launch {
            toggleFavoriteUseCase(apod)
            // Обновляем состояние избранного
            val isFavorite = isFavoriteUseCase(apod.date)
            _uiState.value = _uiState.value.copy(isFavorite = isFavorite)
        }
    }

    suspend fun isFavorite(date: String): Boolean {
        return isFavoriteUseCase(date)
    }
}

data class ApodDetailUiState(
    val selectedApod: Apod? = null,
    val isFavorite: Boolean = false
)
