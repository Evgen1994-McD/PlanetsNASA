package com.example.planets.domain.repository

import androidx.paging.PagingData
import com.example.planets.domain.model.Apod
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с APOD данными

 */
interface ApodRepository {


    fun getApodPagingFlow(): Flow<PagingData<Apod>>
    suspend fun getApodByDate(date: String): Result<Apod>
    suspend fun addToFavorites(apod: Apod): Result<Unit>
    suspend fun removeFromFavorites(date: String): Result<Unit>
    suspend fun toggleFavorite(apod: Apod)
    suspend fun isFavorite(date: String): Boolean
    fun getFavoritesFlow(): Flow<List<Apod>>
    suspend fun getCachedApods(): List<Apod>
    suspend fun clearAllCache(): Result<Unit>
}
