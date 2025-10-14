package com.example.planets.domain.usecase

import com.example.planets.domain.repository.ApodRepository
import javax.inject.Inject

/**
 * Use Case для очистки кэша
 * Инкапсулирует бизнес-логику управления кэшем
 */
class ClearCacheUseCase @Inject constructor(
    private val repository: ApodRepository
) {
    /**
     * Выполняет очистку всего кэша
     */
    suspend operator fun invoke() {
        repository.clearAllCache()
    }
    
    /**
     * Выполняет очистку старого кэша
     */
    suspend fun clearOldCache() {
        repository.clearOldCache()
    }
}
