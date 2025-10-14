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

                // 1. Сначала проверяем, есть ли данные в кэше
                val offset = page * pageSize
                val cachedApods = apodDao.getRecentCachedApods(pageSize, offset)
                val cachedItems = cachedApods.map { it.toApodItem() }
                
                println("ApodPagingSource: Found ${cachedItems.size} cached items for page $page")

                // 2. Проверяем подключение к интернету
                val isOnline = networkMonitor.isOnline()
                println("ApodPagingSource: Network status - isOnline: $isOnline")

                val result: LoadResult<Int, ApodItem> = if (isOnline) {
                    // 3. Если есть интернет, пытаемся загрузить с API
                    try {
                        println("ApodPagingSource: Loading APOD list from API with count $pageSize")
                        val response = apiService.getApodList(API_KEY, pageSize)
                        
                        if (response.isSuccessful) {
                            response.body()?.let { apodResponseList ->
                                println("ApodPagingSource: Received ${apodResponseList.size} APOD items from API")
                                
                                val apodItems = mutableListOf<ApodItem>()
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

                                // Обновляем кэш в репозитории
                                repository.updateCache(apodItems, page == 0)

                                println("ApodPagingSource: Loaded ${apodItems.size} items for page $page from API")

                                LoadResult.Page(
                                    data = apodItems,
                                    prevKey = if (page == 0) null else page - 1,
                                    nextKey = if (apodItems.size < pageSize) null else page + 1
                                )
                            } ?: LoadResult.Error(Exception("Empty response body"))
                        } else {
                            println("ApodPagingSource: API request failed: ${response.code()} ${response.message()}")
                            // API ошибка - проверяем кэш
                            if (cachedItems.isNotEmpty()) {
                                println("ApodPagingSource: API failed, using cached data")
                                LoadResult.Page(
                                    data = cachedItems,
                                    prevKey = if (page == 0) null else page - 1,
                                    nextKey = if (cachedItems.size < pageSize) null else page + 1
                                )
                            } else {
                                // Нет кэша и API не работает - возвращаем ошибку
                                LoadResult.Error(
                                    Exception("HTTP ${response.code()}: ${response.message()}")
                                )
                            }
                        }
                    } catch (e: Exception) {
                        println("ApodPagingSource: Failed to load APOD list: ${e.message}")
                        
                        // Re-throw cancellation exceptions to properly handle coroutine cancellation
                        if (e is CancellationException) {
                            throw e
                        }
                        
                        // Сетевая ошибка - проверяем кэш
                        if (cachedItems.isNotEmpty()) {
                            println("ApodPagingSource: Network error, using cached data")
                            LoadResult.Page(
                                data = cachedItems,
                                prevKey = if (page == 0) null else page - 1,
                                nextKey = if (cachedItems.size < pageSize) null else page + 1
                            )
                        } else {
                            // Нет кэша и сеть не работает - возвращаем ошибку
                            LoadResult.Error(e)
                        }
                    }
                } else {
                    // 4. Нет интернета - используем кэш
                    println("ApodPagingSource: No internet, using cached data")
                    if (cachedItems.isNotEmpty()) {
                        LoadResult.Page(
                            data = cachedItems,
                            prevKey = if (page == 0) null else page - 1,
                            nextKey = if (cachedItems.size < pageSize) null else page + 1
                        )
                    } else {
                        // Нет кэша и нет интернета - возвращаем ошибку отсутствия интернета
                        LoadResult.Error(
                            Exception("No internet connection and no cached data available")
                        )
                    }
                }
                
                result
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
