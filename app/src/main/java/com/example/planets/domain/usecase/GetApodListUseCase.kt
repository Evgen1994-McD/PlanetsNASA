package com.example.planets.domain.usecase

import androidx.paging.PagingData
import com.example.planets.domain.model.Apod
import com.example.planets.domain.repository.ApodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для получения списка APOD с пагинацией
 * Инкапсулирует бизнес-логику получения данных
 */
class GetApodListUseCase @Inject constructor(
    private val repository: ApodRepository
) {
    /**
     * Выполняет получение списка APOD
     * @return Flow с пагинированными данными APOD
     */
    operator fun invoke(): Flow<PagingData<Apod>> {
        return repository.getApodPagingFlow()
    }
}
