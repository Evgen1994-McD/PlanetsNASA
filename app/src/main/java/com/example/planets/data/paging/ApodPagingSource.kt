package com.example.planets.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.planets.data.api.NasaApiService
import com.example.planets.data.database.ApodDao
import com.example.planets.data.database.toApodEntity
import com.example.planets.data.model.ApodItem
import com.example.planets.data.model.toApodItem
import com.example.planets.utils.NetworkMonitor
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
        return try {
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
                    return LoadResult.Page(
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
                
                // Загружаем данные для текущей страницы
                for (i in 0 until pageSize) {
                    val daysBack = (page * pageSize + i).toLong()
                    val targetDate = LocalDate.now().minusDays(daysBack)
                    val dateString = targetDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    
                    try {
                        // Check if we already have this date cached
                        val cachedApod = apodDao.getApodByDate(dateString)
                        if (cachedApod != null) {
                            println("ApodPagingSource: Using cached APOD for $dateString")
                            apodItems.add(cachedApod.toApodItem())
                        } else {
                            // Load from API and cache
                            println("ApodPagingSource: Loading APOD from API for $dateString")
                            val response = apiService.getApod(API_KEY, dateString)
                            if (response.isSuccessful) {
                                response.body()?.let { apodResponse ->
                                    val apodItem = apodResponse.toApodItem()
                                    apodItems.add(apodItem)
                                    // Cache the result
                                    apodDao.insertApod(apodItem.toApodEntity())
                                    println("ApodPagingSource: Successfully loaded APOD for $dateString")
                                }
                            } else {
                                println("ApodPagingSource: API request failed for $dateString: ${response.code()}")
                            }
                        }
                    } catch (e: Exception) {
                        // Skip failed requests and continue with other dates
                        println("ApodPagingSource: Failed to load APOD for date $dateString: ${e.message}")
                    }
                }

                // Обновляем кэш в репозитории
                repository.updateCache(apodItems, page == 0)

                println("ApodPagingSource: Loaded ${apodItems.size} items for page $page")

                LoadResult.Page(
                    data = apodItems,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = page + 1 // Всегда есть следующая страница для APOD
                )
            } else {
                // Load from cache when offline
                val cachedApods = apodDao.getRecentCachedApods(pageSize)
                val apodItems = cachedApods.map { it.toApodItem() }

                LoadResult.Page(
                    data = apodItems,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (apodItems.isEmpty()) null else page + 1
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ApodItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
