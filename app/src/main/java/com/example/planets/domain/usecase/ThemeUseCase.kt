package com.example.planets.domain.usecase

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.example.planets.domain.repository.ThemeRepository
import com.example.planets.domain.model.ThemeMode

/**
 * Интерфейс для управления темой приложения
 */
interface ThemeUseCase {
    fun getThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}


class ThemeUseCaseImpl @Inject constructor(
    private val themeRepository: ThemeRepository
) : ThemeUseCase {
    
    override fun getThemeMode(): Flow<ThemeMode> {
        return themeRepository.getThemeMode()
    }
    
    override suspend fun setThemeMode(mode: ThemeMode) {
        themeRepository.setThemeMode(mode)
    }
}
