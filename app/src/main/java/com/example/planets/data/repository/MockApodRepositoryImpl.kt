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
import com.example.planets.data.mock.MockApodData
import com.example.planets.data.paging.MockApodPagingSource
import com.example.planets.domain.model.Apod
import com.example.planets.domain.repository.ApodRepository
import com.example.planets.utils.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MockApodRepositoryImpl @Inject constructor(
    private val apodDao: ApodDao,
    private val networkMonitor: NetworkMonitor,
    private val context: Context
) : ApodRepository {

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
                MockApodPagingSource(apodDao, networkMonitor)
            }
        ).flow
    }

    override suspend fun getApodByDate(date: String): Result<Apod> = withContext(Dispatchers.IO) {
        try {
            delay(500) // Имитируем задержку сети
            
            val apod = MockApodData.mockApods.find { it.date == date }
            if (apod != null) {
                Result.success(apod)
            } else {
                Result.failure(Exception("APOD с датой $date не найден"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getApodList(count: Int): Result<List<Apod>> = withContext(Dispatchers.IO) {
        try {
            delay(800) // Имитируем задержку сети
            
            val apods = MockApodData.mockApods.take(count)
            Result.success(apods)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

    override suspend fun toggleFavorite(apod: Apod) {
        val isFavorite = apodDao.isFavorite(apod.date)
        if (isFavorite) {
            removeFromFavorites(apod.date).getOrThrow()
        } else {
            addToFavorites(apod).getOrThrow()
        }
    }

    override suspend fun isFavorite(date: String): Boolean {
        return apodDao.isFavorite(date)
    }

    override fun getFavoritesFlow(): Flow<List<Apod>> {
        return apodDao.getAllFavorites().map { favorites ->
            favorites.map { it.toDomain() }
        }
    }

    override suspend fun cacheApod(apod: Apod) = withContext(Dispatchers.IO) {
        apodDao.insertApod(apod.toEntity())
    }

    override suspend fun getCachedApods(): List<Apod> = withContext(Dispatchers.IO) {
        apodDao.getRecentCachedApods(20).map { it.toDomain() }
    }

    override suspend fun clearOldCache(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
            apodDao.deleteOldApods(oneWeekAgo)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearAllCache(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            cachedData.clear()
            lastLoadTime = 0L
            apodDao.deleteAllApods()
            apodDao.deleteAllFavorites()
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
