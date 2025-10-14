package com.example.planets.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.planets.data.mock.MockApodData
import com.example.planets.domain.model.Apod
import kotlinx.coroutines.delay
import android.util.Log


class MockApodPagingSource : PagingSource<Int, Apod>() {
    
    companion object {
        private const val TAG = "MockApodPagingSource"
    }
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Apod> {
        return try {
            Log.d(TAG, "Loading page: ${params.key ?: 0}, size: ${params.loadSize}")
            
            // Имитируем задержку сети
            delay(1000)
            
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            val startIndex = page * pageSize
            val endIndex = minOf(startIndex + pageSize, MockApodData.mockApods.size)
            
            Log.d(TAG, "Loading items from $startIndex to $endIndex")
            
            if (startIndex >= MockApodData.mockApods.size) {
                Log.d(TAG, "No more data available")
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = if (page > 0) page - 1 else null,
                    nextKey = null
                )
            } else {
                val data = MockApodData.mockApods.subList(startIndex, endIndex)
                Log.d(TAG, "Loaded ${data.size} items: ${data.map { it.title }}")
                
                LoadResult.Page(
                    data = data,
                    prevKey = if (page > 0) page - 1 else null,
                    nextKey = if (endIndex < MockApodData.mockApods.size) page + 1 else null
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading page", e)
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, Apod>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
