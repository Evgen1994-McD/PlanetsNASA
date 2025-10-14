package com.example.planets.domain.usecase

import com.example.planets.domain.model.Apod
import com.example.planets.domain.repository.ApodRepository
import javax.inject.Inject

/**
 * Интерфейс для переключения статуса избранного APOD
 */
interface ToggleFavoriteUseCase {
    suspend operator fun invoke(apod: Apod)
}

/**
 * Реализация Use Case для переключения статуса избранного APOD
 */
class ToggleFavoriteUseCaseImpl @Inject constructor(
    private val repository: ApodRepository
) : ToggleFavoriteUseCase {
    
    override suspend operator fun invoke(apod: Apod) {
        val isFavorite = repository.isFavorite(apod.date)
        if (isFavorite) {
            repository.removeFromFavorites(apod.date).getOrThrow()
        } else {
            repository.addToFavorites(apod).getOrThrow()
        }
    }
}