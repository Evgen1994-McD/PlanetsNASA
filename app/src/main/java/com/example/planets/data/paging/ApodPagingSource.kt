package com.example.planets.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.planets.data.api.NasaApiService
import com.example.planets.data.model.ApodItem
import com.example.planets.data.model.toApodItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ApodPagingSource(
    private val apiService: NasaApiService
) : PagingSource<Int, ApodItem>() {

    companion object {
        private const val API_KEY = "cVsJ9alkirbS7Jmj5bA3zFHdopkvdEqnKG45p34o"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ApodItem> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            // NASA APOD API doesn't support pagination directly, so we'll simulate it
            // by requesting dates going backwards from today
            val startDate = LocalDate.now().minusDays((page * pageSize).toLong())
            val endDate = startDate.minusDays((pageSize - 1).toLong())
            
            val apodItems = mutableListOf<ApodItem>()
            
            // Load multiple days of APOD data
            var currentDate = startDate
            while (currentDate >= endDate && apodItems.size < pageSize) {
                try {
                    val dateString = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    val response = apiService.getApod(API_KEY, dateString)
                    if (response.isSuccessful) {
                        response.body()?.let { apodResponse ->
                            apodItems.add(apodResponse.toApodItem())
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
