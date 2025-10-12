package com.example.planets.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.planets.data.api.ApiClient
import com.example.planets.data.model.ApodItem
import com.example.planets.data.model.toApodItem
import com.example.planets.data.paging.ApodPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ApodRepository {
    
    private val apiService = ApiClient.nasaApiService
    
    // Демо API ключ для тестирования
    private val apiKey = "cVsJ9alkirbS7Jmj5bA3zFHdopkvdEqnKG45p34o"
    
    fun getApodPagingFlow(): Flow<PagingData<ApodItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                ApodPagingSource(apiService)
            }
        ).flow
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
