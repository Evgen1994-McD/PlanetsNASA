package com.example.planets.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planets.data.model.ApodItem
import com.example.planets.data.repository.ApodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApodViewModel : ViewModel() {
    
    private val repository = ApodRepository()
    
    private val _uiState = MutableStateFlow(ApodUiState())
    val uiState: StateFlow<ApodUiState> = _uiState.asStateFlow()
    
    init {
        loadApodList()
    }
    
    fun loadApodList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.getApodList(20).fold(
                onSuccess = { apodList ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        apodList = apodList,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Неизвестная ошибка"
                    )
                }
            )
        }
    }
    
    fun refreshApodList() {
        loadApodList()
    }
    
    fun selectApod(apod: ApodItem) {
        _uiState.value = _uiState.value.copy(selectedApod = apod)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ApodUiState(
    val isLoading: Boolean = false,
    val apodList: List<ApodItem> = emptyList(),
    val selectedApod: ApodItem? = null,
    val error: String? = null
)
