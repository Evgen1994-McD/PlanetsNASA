package com.example.planets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planets.domain.model.Apod
import com.example.planets.domain.usecase.GetFavoritesUseCase
import com.example.planets.domain.usecase.IsFavoriteUseCase
import com.example.planets.domain.usecase.ToggleFavoriteUseCase
import com.example.planets.domain.usecase.NotifyCacheClearedUseCase
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
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val notifyCacheClearedUseCase: NotifyCacheClearedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger: StateFlow<Int> = _refreshTrigger.asStateFlow()

    val favoritesFlow: Flow<List<Apod>> = getFavoritesUseCase()

    init {
        // Слушаем уведомления об очистке кэша
        viewModelScope.launch {
            notifyCacheClearedUseCase.getCacheClearedFlow().collect {
                // При очистке кэша сбрасываем состояние ошибок и обновляем данные
                clearError()
                _refreshTrigger.value++
            }
        }
    }

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
