package com.example.planets.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.planets.data.api.ApiClient
import com.example.planets.data.database.ApodDatabase
import com.example.planets.data.database.toApodEntity
import com.example.planets.data.database.toFavoriteEntity
import com.example.planets.data.model.ApodItem
import com.example.planets.data.model.toApodItem
import com.example.planets.data.paging.ApodPagingSource
import com.example.planets.utils.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ApodRepository(private val context: Context) {
    
    private val apiService = ApiClient.nasaApiService
    private val database = ApodDatabase.getDatabase(context)
    private val apodDao = database.apodDao()
    private val networkMonitor = NetworkMonitor(context)
    
    //  API ключ для тестирования
    private val apiKey = "cVsJ9alkirbS7Jmj5bA3zFHdopkvdEqnKG45p34o"
    
    // Кэш для хранения загруженных данных между экземплярами PagingSource
    private val cachedData = mutableListOf<ApodItem>()
    private var lastLoadTime = 0L
    private val cacheTimeout = 5 * 60 * 1000L // 5 минут
    
    fun getApodPagingFlow(): Flow<PagingData<ApodItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 8,
                enablePlaceholders = false,
                prefetchDistance = 3,
                initialLoadSize = 8
            ),
            pagingSourceFactory = { 
                // Создаем новый экземпляр PagingSource для каждого вызова
                ApodPagingSource(apiService, apodDao, networkMonitor, this)
            }
        ).flow
    }
    
    suspend fun cacheApod(apod: ApodItem) = withContext(Dispatchers.IO) {
        apodDao.insertApod(apod.toApodEntity())
    }
    
    suspend fun getCachedApods(): List<ApodItem> = withContext(Dispatchers.IO) {
        apodDao.getRecentCachedApods(50).map { it.toApodItem() }
    }
    
    suspend fun clearOldCache() = withContext(Dispatchers.IO) {
        val oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        apodDao.deleteOldApods(oneWeekAgo)
    }
    
    // Favorites methods
    suspend fun addToFavorites(apod: ApodItem) = withContext(Dispatchers.IO) {
        apodDao.insertFavorite(apod.toFavoriteEntity())
    }
    
    suspend fun removeFromFavorites(date: String) = withContext(Dispatchers.IO) {
        apodDao.deleteFavoriteByDate(date)
    }
    
    suspend fun isFavorite(date: String): Boolean = withContext(Dispatchers.IO) {
        apodDao.isFavorite(date)
    }
    
    fun getFavoritesFlow(): Flow<List<ApodItem>> {
        return apodDao.getAllFavorites().map { favorites ->
            favorites.map { it.toApodItem() }
        }
    }
    
    // Методы для работы с кэшем
    fun getCachedData(): List<ApodItem> = cachedData.toList()
    
    fun isCacheValid(): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastLoadTime) < cacheTimeout && cachedData.isNotEmpty()
    }
    
    fun updateCache(data: List<ApodItem>, isFirstPage: Boolean = false) {
        if (isFirstPage) {
            cachedData.clear()
            cachedData.addAll(data)
        } else {
            cachedData.addAll(data)
        }
        lastLoadTime = System.currentTimeMillis()
    }
    
    fun clearCache() {
        cachedData.clear()
        lastLoadTime = 0L
    }
    
    suspend fun clearAllCache() = withContext(Dispatchers.IO) {
        val apodCountBefore = apodDao.getCachedApodsCount()
        val favoritesCountBefore = apodDao.getFavoritesCount()
        println("ApodRepository: APOD cache count before clear: $apodCountBefore")
        println("ApodRepository: Favorites count before clear: $favoritesCountBefore")
        
        // Очищаем память
        cachedData.clear()
        lastLoadTime = 0L
        
        // Очищаем базу данных - удаляем все APOD и избранные
        apodDao.deleteAllApods()
        apodDao.deleteAllFavorites()
        
        val apodCountAfter = apodDao.getCachedApodsCount()
        val favoritesCountAfter = apodDao.getFavoritesCount()
        println("ApodRepository: APOD cache count after clear: $apodCountAfter")
        println("ApodRepository: Favorites count after clear: $favoritesCountAfter")
        println("ApodRepository: All cache and favorites cleared successfully")
    }
    
    suspend fun getApodList(count: Int = 10): Result<List<ApodItem>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getApodList(apiKey, count)
            if (response.isSuccessful) {
                val apodList = response.body()?.map { it.toApodItem() } ?: emptyList()
                Result.success(apodList)
            } else {
                Result.failure(Exception("Ошибка API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getApodByDate(date: String): Result<ApodItem> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getApod(apiKey, date)
            if (response.isSuccessful) {
                val apod = response.body()?.toApodItem()
                if (apod != null) {
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
    
    suspend fun getRecentApods(days: Int = 7): Result<List<ApodItem>> = withContext(Dispatchers.IO) {
        try {
            val calendar = Calendar.getInstance()
            val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            
            calendar.add(Calendar.DAY_OF_MONTH, -days)
            val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            
            val response = apiService.getApod(apiKey, null, startDate, endDate)
            if (response.isSuccessful) {
                val apod = response.body()?.toApodItem()
                val apodList = if (apod != null) listOf(apod) else emptyList()
                Result.success(apodList)
            } else {
                Result.failure(Exception("Ошибка API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
