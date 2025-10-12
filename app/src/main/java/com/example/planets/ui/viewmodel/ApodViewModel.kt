package com.example.planets.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
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
    
    val apodPagingFlow: Flow<PagingData<ApodItem>> = repository.getApodPagingFlow()
    
    fun selectApod(apod: ApodItem) {
        _uiState.value = _uiState.value.copy(selectedApod = apod)
        // Cache the selected APOD for offline viewing
        viewModelScope.launch {
            repository.cacheApod(apod)
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearOldCache() {
        viewModelScope.launch {
            repository.clearOldCache()
        }
    }
}

data class ApodUiState(
    val selectedApod: ApodItem? = null,
    val error: String? = null
)
