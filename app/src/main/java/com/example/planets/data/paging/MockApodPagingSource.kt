package com.example.planets.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.planets.data.database.ApodDao
import com.example.planets.data.mapper.ApodMapper.toDomain
import com.example.planets.data.mapper.ApodMapper.toEntity
import com.example.planets.data.mock.MockApodData
import com.example.planets.domain.model.Apod
import com.example.planets.utils.NetworkMonitor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Мок PagingSource, который работает точно как реальный API:
 * - Проверяет интернет
 * - Кэширует данные в Room
 * - Показывает экраны ошибок при отсутствии интернета
 * - Использует мок-данные как источник
 */
class MockApodPagingSource(
    private val apodDao: ApodDao,
    private val networkMonitor: NetworkMonitor
) : PagingSource<Int, Apod>() {

    companion object {
        private const val TAG = "MockApodPagingSource"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Apod> {
        return withContext(Dispatchers.IO) {
            try {
                val page = params.key ?: 0
                val pageSize = params.loadSize
                
                Log.d(TAG, "Loading page $page with size $pageSize")

                // 1. Сначала проверяем, есть ли данные в кэше
                val offset = page * pageSize
                val cachedApods = apodDao.getRecentCachedApods(pageSize, offset)
                val cachedItems = cachedApods.map { it.toDomain() }
                Log.d(TAG, "Found ${cachedItems.size} cached items")
                
                // Проверяем общее количество элементов в кэше
                val totalCachedCount = apodDao.getTotalCachedCount()
                Log.d(TAG, "Total cached items: $totalCachedCount")

                // 2. Проверяем подключение к интернету
                val isOnline = networkMonitor.isOnline()
                Log.d(TAG, "Is online: $isOnline")

                val result: LoadResult<Int, Apod> = if (isOnline) {
                    // 3. Если есть интернет, "загружаем" мок-данные
                    try {
                        // Имитируем задержку сети
                        kotlinx.coroutines.delay(1000)

                        val startIndex = page * pageSize
                        val endIndex = minOf(startIndex + pageSize, MockApodData.mockApods.size)

                        if (startIndex >= MockApodData.mockApods.size) {
                            Log.d(TAG, "No more mock data available")
                            LoadResult.Page(
                                data = emptyList(),
                                prevKey = if (page > 0) page - 1 else null,
                                nextKey = null
                            )
                        } else {
                            val mockData = MockApodData.mockApods.subList(startIndex, endIndex)
                            Log.d(TAG, "Loaded ${mockData.size} mock items: ${mockData.map { it.title }}")

                            // Кэшируем каждый элемент
                            mockData.forEach { apod ->
                                try {
                                    apodDao.insertApod(apod.toEntity())
                                } catch (e: Exception) {
                                    Log.w(TAG, "Failed to cache item: ${apod.title}")
                                }
                            }
                            //"Загрузили данные"
                            LoadResult.Page(
                                data = mockData,
                                prevKey = if (page == 0) null else page - 1,
                                nextKey = if (mockData.size < pageSize) null else page + 1
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading mock data", e)

                        // Re-throw cancellation exceptions
                        if (e is CancellationException) {
                            throw e
                        }

                        // Сетевая ошибка - проверяем кэш
                        if (cachedItems.isNotEmpty()) {
                            LoadResult.Page(
                                data = cachedItems,
                                prevKey = if (page == 0) null else page - 1,
                                nextKey = if (cachedItems.size < pageSize) null else page + 1
                            )
                        } else {
                            LoadResult.Error(e)
                        }
                    }
                } else {
                    // 4. Нет интернета - используем кэш
                    if (cachedItems.isNotEmpty()) {
                        Log.d(TAG, "Using cached data (offline mode)")
                        LoadResult.Page(
                            data = cachedItems,
                            prevKey = if (page == 0) null else page - 1,
                            nextKey = if (cachedItems.size < pageSize) null else page + 1
                        )
                    } else {
                        Log.d(TAG, "No cached data available (offline mode)")
                        LoadResult.Error(
                            Exception("No internet connection and no cached data available")
                        )
                    }
                }
                
                result
            } catch (e: Exception) {
                Log.e(TAG, "Error in load method", e)
                
                // Re-throw cancellation exceptions
                if (e is CancellationException) {
                    throw e
                }
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Apod>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
