package com.example.planets.domain.repository

import com.example.planets.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для управления темой
 */
interface ThemeRepository {
    fun getThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
