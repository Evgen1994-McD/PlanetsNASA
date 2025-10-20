package com.example.planets.domain.model

/**
 * Domain модель для астрономической фотографии дня (APOD)
 * Представляет бизнес-сущность без зависимостей от внешних фреймворков
 */
data class Apod(
    val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdUrl: String?,
    val mediaType: String,
    val serviceVersion: String
)