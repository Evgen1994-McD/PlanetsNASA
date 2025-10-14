package com.example.planets.domain.usecase

import com.example.planets.domain.model.Apod
import com.example.planets.domain.repository.ApodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case для получения списка избранных APOD
 * Инкапсулирует бизнес-логику получения избранных элементов
 */
class GetFavoritesUseCase @Inject constructor(
    private val repository: ApodRepository
) {
    /**
     * Выполняет получение списка избранных APOD
     * @return Flow со списком избранных APOD
     */
    operator fun invoke(): Flow<List<Apod>> {
        return repository.getFavoritesFlow()
    }
}
