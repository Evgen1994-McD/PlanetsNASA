package com.example.planets.domain.usecase

import androidx.paging.PagingData
import com.example.planets.domain.model.Apod
import com.example.planets.domain.repository.ApodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Интерфейс для получения списка APOD с пагинацией
 */
interface GetApodListUseCase {
    operator fun invoke(): Flow<PagingData<Apod>>
    fun getRefreshableFlow(refreshTrigger: Flow<Unit>): Flow<PagingData<Apod>>
}

class GetApodListUseCaseImpl @Inject constructor(
    private val repository: ApodRepository
) : GetApodListUseCase {
    
    override operator fun invoke(): Flow<PagingData<Apod>> {
        return repository.getApodPagingFlow()
    }
    
    override fun getRefreshableFlow(refreshTrigger: Flow<Unit>): Flow<PagingData<Apod>> {
        return refreshTrigger.flatMapLatest {
            repository.getApodPagingFlow()
        }
    }
}