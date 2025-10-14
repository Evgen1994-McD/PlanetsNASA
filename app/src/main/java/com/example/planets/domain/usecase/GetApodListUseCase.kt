package com.example.planets.domain.usecase

import androidx.paging.PagingData
import com.example.planets.domain.model.Apod
import com.example.planets.domain.repository.ApodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Интерфейс для получения списка APOD с пагинацией
 */
interface GetApodListUseCase {
    operator fun invoke(): Flow<PagingData<Apod>>
}

class GetApodListUseCaseImpl @Inject constructor(
    private val repository: ApodRepository
) : GetApodListUseCase {
    
    override operator fun invoke(): Flow<PagingData<Apod>> {
        return repository.getApodPagingFlow()
    }
}