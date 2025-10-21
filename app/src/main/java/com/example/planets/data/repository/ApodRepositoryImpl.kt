package com.example.planets.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.planets.data.api.NasaApiService
import com.example.planets.data.database.ApodDao
import com.example.planets.data.mapper.ApodMapper.toDomain
import com.example.planets.data.mapper.ApodMapper.toEntity
import com.example.planets.data.mapper.ApodMapper.toFavoriteEntity
import com.example.planets.data.paging.ApodPagingSource
import com.example.planets.domain.model.Apod
import com.example.planets.domain.repository.ApodRepository
import com.example.planets.domain.usecase.NotifyCacheClearedUseCase
import com.example.planets.utils.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/***
 * Реализация репозитория для работы с APOD данными
 * Реализует интерфейс ApodRepository из Domain Layer
 */
@Singleton
class ApodRepositoryImpl @Inject constructor(
    private val apiService: NasaApiService,
    private val apodDao: ApodDao,
    private val networkMonitor: NetworkMonitor,
    private val context: Context,
    private val notifyCacheClearedUseCase: NotifyCacheClearedUseCase
) : ApodRepository {
    
    // API ключ для тестирования
    private val apiKey = "cVsJ9alkirbS7Jmj5bA3zFHdopkvdEqnKG45p34o"
    
    // Кэш для хранения загруженных данных между экземплярами PagingSource
    private val cachedData = mutableListOf<Apod>()
    private var lastLoadTime = 0L
    private val cacheTimeout = 5 * 60 * 1000L // 5 минут
    
    override fun getApodPagingFlow(): Flow<PagingData<Apod>> {
        return Pager(
            config = PagingConfig(
                pageSize = 4,
                enablePlaceholders = false,
                prefetchDistance = 2,
                initialLoadSize = 4
            ),
            pagingSourceFactory = { 
                ApodPagingSource(apiService, apodDao, networkMonitor, this)
            }
        ).flow
    }


    
    override suspend fun getApodByDate(date: String): Result<Apod> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getApod(apiKey, date)
            if (response.isSuccessful) {
                val apodResponse = response.body()
                if (apodResponse != null) {
                    val apod = apodResponse.toDomain()
                    Result.success(apod)
                } else {
                    Result.failure(Exception("Данные не найдены"))
                }
            } else {
                Result.failure(Exception("Ошибка API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    

    
    override suspend fun toggleFavorite(apod: Apod) = withContext(Dispatchers.IO) {
        val isFavorite = apodDao.isFavorite(apod.date)
        if (isFavorite) {
            apodDao.deleteFavoriteByDate(apod.date)
        } else {
            apodDao.insertFavorite(apod.toFavoriteEntity())
        }
    }
    
    override suspend fun isFavorite(date: String): Boolean = withContext(Dispatchers.IO) {
        apodDao.isFavorite(date)
    }
    
    override suspend fun addToFavorites(apod: Apod): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apodDao.insertFavorite(apod.toFavoriteEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun removeFromFavorites(date: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apodDao.deleteFavoriteByDate(date)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getFavoritesFlow(): Flow<List<Apod>> {
        return apodDao.getAllFavorites().map { favorites ->
            favorites.map { it.toDomain() }
        }
    }
    

    override suspend fun getCachedApods(): List<Apod> = withContext(Dispatchers.IO) {
        apodDao.getRecentCachedApods(50).map { it.toDomain() }
    }
    

    
    override suspend fun clearAllCache(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Очищаем память
            cachedData.clear()
            lastLoadTime = 0L
            
            // Очищаем базу данных - удаляем все APOD и избранные
            apodDao.deleteAllApods()
            apodDao.deleteAllFavorites()
            notifyCacheClearedUseCase.notifyCacheCleared()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Методы для работы с кэшем (используются PagingSource)
    fun updateCache(data: List<Apod>, isFirstPage: Boolean = false) {
        if (isFirstPage) {
            cachedData.clear()
            cachedData.addAll(data)
        } else {
            cachedData.addAll(data)
        }
        lastLoadTime = System.currentTimeMillis()
    }
}
