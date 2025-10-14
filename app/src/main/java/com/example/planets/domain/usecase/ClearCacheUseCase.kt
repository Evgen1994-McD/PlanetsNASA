package com.example.planets.domain.usecase

import com.example.planets.domain.repository.ApodRepository
import com.example.planets.domain.usecase.NotifyCacheClearedUseCase
import javax.inject.Inject

/**
 * Интерфейс для очистки кэша APOD
 */
interface ClearCacheUseCase {
    suspend operator fun invoke(): Result<Unit>
    suspend fun clearOldCache(): Result<Unit>
}


class ClearCacheUseCaseImpl @Inject constructor(
    private val repository: ApodRepository,
    private val notifyCacheClearedUseCase: NotifyCacheClearedUseCase
) : ClearCacheUseCase {
    
    override suspend operator fun invoke(): Result<Unit> {
        val result = repository.clearAllCache()
        if (result.isSuccess) {
            notifyCacheClearedUseCase.notifyCacheCleared()
        }
        return result
    }
    
    override suspend fun clearOldCache(): Result<Unit> {
        val result = repository.clearOldCache()
        if (result.isSuccess) {
            notifyCacheClearedUseCase.notifyCacheCleared()
        }
        return result
    }
}