package com.example.planets.domain.usecase

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use Case для инвалидации PagingSource при очистке кэша
 */
interface InvalidatePagingSourceUseCase {
    val invalidateTrigger: SharedFlow<Unit>
    suspend fun invalidatePagingSource()
}

@Singleton
class InvalidatePagingSourceUseCaseImpl @Inject constructor() : InvalidatePagingSourceUseCase {
    
    private val _invalidateTrigger = MutableSharedFlow<Unit>()
    override val invalidateTrigger: SharedFlow<Unit> = _invalidateTrigger.asSharedFlow()
    
    override suspend fun invalidatePagingSource() {
        _invalidateTrigger.emit(Unit)
    }
}
