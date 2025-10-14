package com.example.planets.domain.usecase

import com.example.planets.domain.model.Apod
import com.example.planets.domain.repository.ApodRepository
import javax.inject.Inject

/**
 * Use Case для получения детальной информации об APOD
 * Инкапсулирует бизнес-логику получения APOD по дате
 */
class GetApodDetailUseCase @Inject constructor(
    private val repository: ApodRepository
) {
    /**
     * Выполняет получение APOD по дате
     * @param date дата в формате YYYY-MM-DD
     * @return Result с APOD или ошибкой
     */
    suspend operator fun invoke(date: String): Result<Apod> {
        return repository.getApodByDate(date)
    }
}
