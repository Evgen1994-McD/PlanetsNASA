package com.example.planets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.planets.domain.model.Apod
import com.example.planets.domain.usecase.GetApodListUseCase
import com.example.planets.domain.usecase.IsFavoriteUseCase
import com.example.planets.domain.usecase.ToggleFavoriteUseCase
import com.example.planets.domain.usecase.NotifyCacheClearedUseCase
import com.example.planets.domain.usecase.InvalidatePagingSourceUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ApodListViewModel @Inject constructor(
    private val getApodListUseCase: GetApodListUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val notifyCacheClearedUseCase: NotifyCacheClearedUseCase,
    private val invalidatePagingSourceUseCase: InvalidatePagingSourceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApodListUiState())
    val uiState: StateFlow<ApodListUiState> = _uiState.asStateFlow()

    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger: StateFlow<Int> = _refreshTrigger.asStateFlow()

    val apodPagingFlow: Flow<PagingData<Apod>> = getApodListUseCase.getRefreshableFlow(
        refreshTrigger.map { Unit }
    ).cachedIn(viewModelScope)

    init {
        // Слушаем уведомления об очистке кэша
        viewModelScope.launch {
            notifyCacheClearedUseCase.getCacheClearedFlow().collect {
                // При очистке кэша сбрасываем состояние ошибок и обновляем данные
                clearError()
                _refreshTrigger.value++
            }
        }
        
        // Слушаем инвалидацию PagingSource
        viewModelScope.launch {
            invalidatePagingSourceUseCase.invalidateTrigger.collect {
                // При инвалидации PagingSource обновляем данные
                _refreshTrigger.value++
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            error = null,
            hasNetworkError = false,
            hasHttpError = false,
            httpErrorCode = null,
            isRetrying = false
        )
    }

    fun setNetworkError() {
        _uiState.value = _uiState.value.copy(
            hasNetworkError = true,
            isLoading = false,
            isRetrying = false
        )
    }

    fun setHttpError(errorCode: Int) {
        _uiState.value = _uiState.value.copy(
            hasHttpError = true,
            httpErrorCode = errorCode,
            isLoading = false,
            isRetrying = false
        )
    }

    fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loading)
    }

    fun setRetrying(retrying: Boolean) {
        _uiState.value = _uiState.value.copy(isRetrying = retrying)
    }

    fun toggleFavorite(apod: Apod) {
        viewModelScope.launch {
            toggleFavoriteUseCase(apod)
        }
    }

    suspend fun isFavorite(date: String): Boolean {
        return isFavoriteUseCase(date)
    }
}

data class ApodListUiState(
    val error: String? = null,
    val isLoading: Boolean = false,
    val isRetrying: Boolean = false,
    val hasNetworkError: Boolean = false,
    val hasHttpError: Boolean = false,
    val httpErrorCode: Int? = null
)
