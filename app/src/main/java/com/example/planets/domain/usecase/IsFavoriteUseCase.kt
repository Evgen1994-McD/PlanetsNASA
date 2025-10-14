package com.example.planets.domain.usecase

import com.example.planets.domain.repository.ApodRepository
import javax.inject.Inject

/**
 * Use Case для проверки статуса избранного APOD
 * Инкапсулирует бизнес-логику проверки избранного статуса
 */
class IsFavoriteUseCase @Inject constructor(
    private val repository: ApodRepository
) {
    /**
     * Выполняет проверку, находится ли APOD в избранном
     * @param date дата APOD
     * @return true если в избранном, false иначе
     */
    suspend operator fun invoke(date: String): Boolean {
        return repository.isFavorite(date)
    }
}
