package com.example.planets.domain.usecase

import com.example.planets.domain.model.Apod
import com.example.planets.domain.repository.ApodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Интерфейс для получения списка избранных APOD
 */
interface GetFavoritesUseCase {
    operator fun invoke(): Flow<List<Apod>>
}


class GetFavoritesUseCaseImpl @Inject constructor(
    private val repository: ApodRepository
) : GetFavoritesUseCase {
    
    override operator fun invoke(): Flow<List<Apod>> {
        return repository.getFavoritesFlow()
    }
}