package com.example.planets.domain.usecase

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Интерфейс для уведомления об очистке кэша
 */
interface NotifyCacheClearedUseCase {
    fun getCacheClearedFlow(): Flow<Unit>
    suspend fun notifyCacheCleared()
}

/**
 * Реализация Use Case для уведомления об очистке кэша
 */
class NotifyCacheClearedUseCaseImpl @Inject constructor() : NotifyCacheClearedUseCase {
    
    private val _cacheClearedFlow = kotlinx.coroutines.flow.MutableSharedFlow<Unit>()
    
    override fun getCacheClearedFlow(): Flow<Unit> = _cacheClearedFlow
    
    override suspend fun notifyCacheCleared() {
        _cacheClearedFlow.emit(Unit)
    }
}
