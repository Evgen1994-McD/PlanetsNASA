package com.example.planets.domain.usecase

import com.example.planets.domain.repository.ApodRepository
import javax.inject.Inject

/**
 * Интерфейс для очистки кэша APOD
 */
interface ClearCacheUseCase {
    suspend operator fun invoke(): Result<Unit>
}


class ClearCacheUseCaseImpl @Inject constructor(
    private val repository: ApodRepository,
    private val notifyCacheClearedUseCase: NotifyCacheClearedUseCase,
    private val invalidatePagingSourceUseCase: InvalidatePagingSourceUseCase
) : ClearCacheUseCase {
    
    override suspend operator fun invoke(): Result<Unit> {
        val result = repository.clearAllCache()
        if (result.isSuccess) {
            notifyCacheClearedUseCase.notifyCacheCleared()
            invalidatePagingSourceUseCase.invalidatePagingSource()
        }
        return result
    }
    

}