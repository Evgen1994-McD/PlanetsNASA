package com.example.planets.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.planets.data.api.ApiClient
import com.example.planets.data.database.ApodDatabase
import com.example.planets.data.repository.ApodRepositoryImpl
import com.example.planets.domain.usecase.ClearCacheUseCase
import com.example.planets.domain.usecase.GetApodListUseCase
import com.example.planets.domain.usecase.GetFavoritesUseCase
import com.example.planets.domain.usecase.IsFavoriteUseCase
import com.example.planets.domain.usecase.ToggleFavoriteUseCase
import com.example.planets.utils.NetworkMonitor

/**
 * Фабрика для создания ApodViewModel с Use Cases
 * Временное решение до внедрения DI
 */
class ApodViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApodViewModel::class.java)) {
            // Создаем зависимости
            val apiService = ApiClient.nasaApiService
            val database = ApodDatabase.getDatabase(application)
            val apodDao = database.apodDao()
            val networkMonitor = NetworkMonitor(application)
            
            // Создаем репозиторий
            val repository = ApodRepositoryImpl(
                apiService = apiService,
                apodDao = apodDao,
                networkMonitor = networkMonitor,
                context = application
            )
            
            // Создаем Use Cases
            val getApodListUseCase = GetApodListUseCase(repository)
            val toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)
            val isFavoriteUseCase = IsFavoriteUseCase(repository)
            val getFavoritesUseCase = GetFavoritesUseCase(repository)
            val clearCacheUseCase = ClearCacheUseCase(repository)
            
            // Создаем ViewModel
            return ApodViewModel(
                getApodListUseCase = getApodListUseCase,
                toggleFavoriteUseCase = toggleFavoriteUseCase,
                isFavoriteUseCase = isFavoriteUseCase,
                getFavoritesUseCase = getFavoritesUseCase,
                clearCacheUseCase = clearCacheUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
