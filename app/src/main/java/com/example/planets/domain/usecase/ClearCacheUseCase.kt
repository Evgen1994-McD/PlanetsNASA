package com.example.planets.domain.usecase

import com.example.planets.domain.repository.ApodRepository
import javax.inject.Inject

/**
 * Интерфейс для очистки кэша APOD
 */
interface ClearCacheUseCase {
    suspend operator fun invoke(): Result<Unit>
    suspend fun clearOldCache(): Result<Unit>
}


class ClearCacheUseCaseImpl @Inject constructor(
    private val repository: ApodRepository
) : ClearCacheUseCase {
    
    override suspend operator fun invoke(): Result<Unit> {
        return repository.clearAllCache()
    }
    
    override suspend fun clearOldCache(): Result<Unit> {
        return repository.clearOldCache()
    }
}