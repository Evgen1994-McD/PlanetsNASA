package com.example.planets.domain.repository

import androidx.paging.PagingData
import com.example.planets.domain.model.Apod
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с APOD данными
 * Определяет контракт для получения данных без привязки к конкретной реализации
 */
interface ApodRepository {
    
    /**
     * Получает поток данных APOD с пагинацией
     * @return Flow с пагинированными данными
     */
    fun getApodPagingFlow(): Flow<PagingData<Apod>>
    
    /**
     * Получает APOD по конкретной дате
     * @param date дата в формате YYYY-MM-DD
     * @return Result с APOD или ошибкой
     */
    suspend fun getApodByDate(date: String): Result<Apod>
    
    /**
     * Получает список APOD за последние дни
     * @param count количество дней
     * @return Result со списком APOD или ошибкой
     */
    suspend fun getApodList(count: Int = 10): Result<List<Apod>>
    
    /**
     * Переключает статус избранного для APOD
     * @param apod APOD для переключения
     */
    suspend fun toggleFavorite(apod: Apod)
    
    /**
     * Проверяет, находится ли APOD в избранном
     * @param date дата APOD
     * @return true если в избранном, false иначе
     */
    suspend fun isFavorite(date: String): Boolean
    
    /**
     * Получает поток избранных APOD
     * @return Flow со списком избранных APOD
     */
    fun getFavoritesFlow(): Flow<List<Apod>>
    
    /**
     * Кэширует APOD для офлайн просмотра
     * @param apod APOD для кэширования
     */
    suspend fun cacheApod(apod: Apod)
    
    /**
     * Получает кэшированные APOD
     * @return список кэшированных APOD
     */
    suspend fun getCachedApods(): List<Apod>
    
    /**
     * Очищает старый кэш
     */
    suspend fun clearOldCache()
    
    /**
     * Очищает весь кэш
     */
    suspend fun clearAllCache()
}
