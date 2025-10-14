package com.example.planets.domain.error

/**
 * Базовый класс для всех доменных ошибок
 */
sealed class ApodError : Exception() {
    
    /**
     * Ошибка сети
     */
    data class NetworkError(
        override val message: String = "Ошибка сети"
    ) : ApodError()
    
    /**
     * Ошибка API
     */
    data class ApiError(
        val code: Int,
        override val message: String = "Ошибка API: $code"
    ) : ApodError()
    
    /**
     * Ошибка кэша
     */
    data class CacheError(
        override val message: String = "Ошибка кэша"
    ) : ApodError()
    
    /**
     * Данные не найдены
     */
    data class NotFoundError(
        override val message: String = "Данные не найдены"
    ) : ApodError()
    
    /**
     * Неизвестная ошибка
     */
    data class UnknownError(
        override val message: String = "Неизвестная ошибка"
    ) : ApodError()
}
