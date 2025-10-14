package com.example.planets.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.planets.domain.model.Apod
import com.example.planets.domain.usecase.ClearCacheUseCase
import com.example.planets.domain.usecase.GetApodListUseCase
import com.example.planets.domain.usecase.GetFavoritesUseCase
import com.example.planets.domain.usecase.IsFavoriteUseCase
import com.example.planets.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApodViewModel(
    private val getApodListUseCase: GetApodListUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val clearCacheUseCase: ClearCacheUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ApodUiState())
    val uiState: StateFlow<ApodUiState> = _uiState.asStateFlow()

    // Создаем PagingSource один раз и переиспользуем его
    val apodPagingFlow: Flow<PagingData<Apod>> = getApodListUseCase()
        .cachedIn(viewModelScope)
    
    // Favorites flow
    val favoritesFlow: Flow<List<Apod>> = getFavoritesUseCase()
    
    fun selectApod(apod: Apod) {
        _uiState.value = _uiState.value.copy(selectedApod = apod)
    }
    
    fun clearSelectedApod() {
        _uiState.value = _uiState.value.copy(selectedApod = null)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(
            error = null,
            hasNetworkError = false,
            hasHttpError = false,
            httpErrorCode = null
        )
    }
    
    fun setNetworkError() {
        _uiState.value = _uiState.value.copy(
            hasNetworkError = true,
            isLoading = false
        )
    }
    
    fun setHttpError(errorCode: Int) {
        _uiState.value = _uiState.value.copy(
            hasHttpError = true,
            httpErrorCode = errorCode,
            isLoading = false
        )
    }
    
    fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loading)
    }
    
    fun clearOldCache() {
        viewModelScope.launch {
            clearCacheUseCase.clearOldCache()
        }
    }
    
    fun clearAllCache() {
        viewModelScope.launch {
            clearCacheUseCase()
        }
    }
    
    // Favorites methods
    fun toggleFavorite(apod: Apod) {
        viewModelScope.launch {
            toggleFavoriteUseCase(apod)
        }
    }
    
    suspend fun isFavorite(date: String): Boolean {
        return isFavoriteUseCase(date)
    }
}

data class ApodUiState(
    val selectedApod: Apod? = null,
    val error: String? = null,
    val isLoading: Boolean = false,
    val hasNetworkError: Boolean = false,
    val hasHttpError: Boolean = false,
    val httpErrorCode: Int? = null
)
