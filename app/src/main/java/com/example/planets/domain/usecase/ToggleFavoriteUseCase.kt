package com.example.planets.domain.usecase

import com.example.planets.domain.model.Apod
import com.example.planets.domain.repository.ApodRepository
import javax.inject.Inject

/**
 * Use Case для переключения статуса избранного APOD
 * Инкапсулирует бизнес-логику управления избранными элементами
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: ApodRepository
) {
    /**
     * Выполняет переключение статуса избранного для APOD
     * @param apod APOD для переключения
     */
    suspend operator fun invoke(apod: Apod) {
        repository.toggleFavorite(apod)
    }
}
