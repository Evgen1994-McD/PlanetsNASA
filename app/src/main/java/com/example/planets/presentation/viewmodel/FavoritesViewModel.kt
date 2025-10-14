package com.example.planets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planets.domain.model.Apod
import com.example.planets.domain.usecase.GetFavoritesUseCase
import com.example.planets.domain.usecase.IsFavoriteUseCase
import com.example.planets.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    val favoritesFlow: Flow<List<Apod>> = getFavoritesUseCase()

    fun toggleFavorite(apod: Apod) {
        viewModelScope.launch {
            toggleFavoriteUseCase(apod)
        }
    }

    suspend fun isFavorite(date: String): Boolean {
        return isFavoriteUseCase(date)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun setError(error: String) {
        _uiState.value = _uiState.value.copy(error = error)
    }
}

data class FavoritesUiState(
    val error: String? = null
)
