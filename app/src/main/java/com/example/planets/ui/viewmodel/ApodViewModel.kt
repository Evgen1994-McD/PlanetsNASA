package com.example.planets.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.planets.data.model.ApodItem
import com.example.planets.data.repository.ApodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApodViewModel : ViewModel() {
    
    private val repository = ApodRepository()
    
    private val _uiState = MutableStateFlow(ApodUiState())
    val uiState: StateFlow<ApodUiState> = _uiState.asStateFlow()
    
    val apodPagingFlow: Flow<PagingData<ApodItem>> = repository.getApodPagingFlow()
    
    fun selectApod(apod: ApodItem) {
        _uiState.value = _uiState.value.copy(selectedApod = apod)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ApodUiState(
    val selectedApod: ApodItem? = null,
    val error: String? = null
)
