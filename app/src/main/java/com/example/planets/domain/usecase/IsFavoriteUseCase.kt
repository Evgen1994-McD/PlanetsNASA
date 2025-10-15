package com.example.planets.domain.usecase

import com.example.planets.domain.repository.ApodRepository
import javax.inject.Inject

/**
 * Интерфейс для проверки статуса избранного APOD
 */
interface IsFavoriteUseCase {
    suspend operator fun invoke(date: String): Boolean
}


class IsFavoriteUseCaseImpl @Inject constructor(
    private val repository: ApodRepository
) : IsFavoriteUseCase {
    
    override suspend operator fun invoke(date: String): Boolean {
        return repository.isFavorite(date)
    }
}