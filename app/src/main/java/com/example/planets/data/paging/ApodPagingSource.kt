package com.example.planets.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.planets.data.api.NasaApiService
import com.example.planets.data.database.ApodDao
import com.example.planets.data.database.toApodEntity
import com.example.planets.data.model.ApodItem
import com.example.planets.data.model.toApodItem
import com.example.planets.utils.NetworkMonitor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ApodPagingSource(
    private val apiService: NasaApiService,
    private val apodDao: ApodDao,
    private val networkMonitor: NetworkMonitor,
    private val repository: com.example.planets.data.repository.ApodRepository
) : PagingSource<Int, ApodItem>() {

    companion object {
        private const val API_KEY = "cVsJ9alkirbS7Jmj5bA3zFHdopkvdEqnKG45p34o"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ApodItem> {
        return withContext(Dispatchers.IO) {
            try {
                val page = params.key ?: 0
                val pageSize = params.loadSize
                
                println("ApodPagingSource: Loading page $page with size $pageSize")

                // Проверяем кэш только для первой страницы
                if (page == 0 && repository.isCacheValid()) {
                    val cachedData = repository.getCachedData()
                    val startIndex = page * pageSize
                    val endIndex = minOf(startIndex + pageSize, cachedData.size)
                    
                    println("ApodPagingSource: Using cache for page $page, cached items: ${cachedData.size}")
                    
                    if (startIndex < cachedData.size) {
                        val pageData = cachedData.subList(startIndex, endIndex)
                        return@withContext LoadResult.Page(
                            data = pageData,
                            prevKey = null,
                            nextKey = page + 1 // Всегда есть следующая страница для APOD
                        )
                    }
                }

                // Check if we have internet connection
                val isOnline = networkMonitor.isOnline()

                if (isOnline) {
                    // Load from API and cache the results
                    val apodItems = mutableListOf<ApodItem>()
                    
                    try {
                        // Загружаем список APOD с API
                        println("ApodPagingSource: Loading APOD list from API with count $pageSize")
                        val response = apiService.getApodList(API_KEY, pageSize)
                        
                        if (response.isSuccessful) {
                            response.body()?.let { apodResponseList ->
                                println("ApodPagingSource: Received ${apodResponseList.size} APOD items from API")
                                
                                apodResponseList.forEach { apodResponse ->
                                    val apodItem = apodResponse.toApodItem()
                                    apodItems.add(apodItem)
                                    
                                    // Кэшируем каждый элемент
                                    try {
                                        apodDao.insertApod(apodItem.toApodEntity())
                                    } catch (e: Exception) {
                                        println("ApodPagingSource: Failed to cache APOD for ${apodItem.date}: ${e.message}")
                                    }
                                }
                            }
                        } else {
                            println("ApodPagingSource: API request failed: ${response.code()} ${response.message()}")
                            return@withContext LoadResult.Error(
                                Exception("HTTP ${response.code()}: ${response.message()}")
                            )
                        }
                    } catch (e: Exception) {
                        println("ApodPagingSource: Failed to load APOD list: ${e.message}")
                        
                        // Re-throw cancellation exceptions to properly handle coroutine cancellation
                        if (e is CancellationException) {
                            throw e
                        }
                        
                        return@withContext LoadResult.Error(e)
                    }

                    // Обновляем кэш в репозитории
                    repository.updateCache(apodItems, page == 0)

                    println("ApodPagingSource: Loaded ${apodItems.size} items for page $page")

                    LoadResult.Page(
                        data = apodItems,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (apodItems.size < pageSize) null else page + 1
                    )
                } else {
                    // Load from cache when offline
                    val offset = page * pageSize
                    val cachedApods = apodDao.getRecentCachedApods(pageSize, offset)
                    val apodItems = cachedApods.map { it.toApodItem() }

                    LoadResult.Page(
                        data = apodItems,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (apodItems.size < pageSize) null else page + 1
                    )
                }
            } catch (e: Exception) {
                // Re-throw cancellation exceptions to properly handle coroutine cancellation
                if (e is CancellationException) {
                    throw e
                }
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ApodItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
