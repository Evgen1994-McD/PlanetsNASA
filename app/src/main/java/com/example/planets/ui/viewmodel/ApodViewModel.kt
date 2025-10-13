package com.example.planets.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.planets.data.model.ApodItem
import com.example.planets.data.repository.ApodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApodViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = ApodRepository(application)

    private val _uiState = MutableStateFlow(ApodUiState())
    val uiState: StateFlow<ApodUiState> = _uiState.asStateFlow()

    // Создаем PagingSource один раз и переиспользуем его
    val apodPagingFlow: Flow<PagingData<ApodItem>> = repository.getApodPagingFlow()
        .cachedIn(viewModelScope)
    
    // Favorites flow
    val favoritesFlow: Flow<List<ApodItem>> = repository.getFavoritesFlow()
    
    fun selectApod(apod: ApodItem) {
        _uiState.value = _uiState.value.copy(selectedApod = apod)
        // Cache the selected APOD for offline viewing
        viewModelScope.launch {
            repository.cacheApod(apod)
        }
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
            repository.clearOldCache()
        }
    }
    
    fun clearAllCache() {
        viewModelScope.launch {
            println("ApodViewModel: Starting cache clear...")
            repository.clearAllCache()
            println("ApodViewModel: Cache clear completed")
        }
    }
    
    // Favorites methods
    fun toggleFavorite(apod: ApodItem) {
        viewModelScope.launch {
            val isFavorite = repository.isFavorite(apod.date)
            if (isFavorite) {
                repository.removeFromFavorites(apod.date)
            } else {
                repository.addToFavorites(apod)
            }
        }
    }
    
    suspend fun isFavorite(date: String): Boolean {
        return repository.isFavorite(date)
    }
}

data class ApodUiState(
    val selectedApod: ApodItem? = null,
    val error: String? = null,
    val isLoading: Boolean = false,
    val hasNetworkError: Boolean = false,
    val hasHttpError: Boolean = false,
    val httpErrorCode: Int? = null
)
