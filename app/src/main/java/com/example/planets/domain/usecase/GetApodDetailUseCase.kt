package com.example.planets.domain.usecase

import com.example.planets.domain.model.Apod
import com.example.planets.domain.repository.ApodRepository
import javax.inject.Inject

/**
 * Интерфейс для получения детальной информации об APOD по дате
 */
interface GetApodDetailUseCase {
    suspend operator fun invoke(date: String): Result<Apod>
}


class GetApodDetailUseCaseImpl @Inject constructor(
    private val repository: ApodRepository
) : GetApodDetailUseCase {
    
    override suspend operator fun invoke(date: String): Result<Apod> {
        return repository.getApodByDate(date)
    }
}