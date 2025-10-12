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
    private val networkMonitor: NetworkMonitor
) : PagingSource<Int, ApodItem>() {

    companion object {
        private const val API_KEY = "cVsJ9alkirbS7Jmj5bA3zFHdopkvdEqnKG45p34o"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ApodItem> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            // Check if we have internet connection
            val isOnline = networkMonitor.isOnline()
            
            if (isOnline) {
                // Load from API and cache the results
                val startDate = LocalDate.now().minusDays((page * pageSize).toLong())
                val endDate = startDate.minusDays((pageSize - 1).toLong())
                
                val apodItems = mutableListOf<ApodItem>()
                
                // Load multiple days of APOD data
                var currentDate = startDate
                while (currentDate >= endDate && apodItems.size < pageSize) {
                    try {
                        val dateString = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        
                        // Check if we already have this date cached
                        val cachedApod = apodDao.getApodByDate(dateString)
                        if (cachedApod != null) {
                            apodItems.add(cachedApod.toApodItem())
                        } else {
                            // Load from API and cache
                            val response = apiService.getApod(API_KEY, dateString)
                            if (response.isSuccessful) {
                                response.body()?.let { apodResponse ->
                                    val apodItem = apodResponse.toApodItem()
                                    apodItems.add(apodItem)
                                    // Cache the result
                                    apodDao.insertApod(apodItem.toApodEntity())
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Skip failed requests and continue with other dates
                    }
                    currentDate = currentDate.minusDays(1)
                }
                
                LoadResult.Page(
                    data = apodItems,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (apodItems.isEmpty()) null else page + 1
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
