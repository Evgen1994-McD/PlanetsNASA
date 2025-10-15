package com.example.planets.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.planets.domain.repository.ThemeRepository
import com.example.planets.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ThemeRepository {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    private val themeKey = "theme_mode"
    
    private val _themeMode = MutableStateFlow(getStoredThemeMode())
    override fun getThemeMode(): Flow<ThemeMode> = _themeMode.asStateFlow()
    
    override suspend fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(themeKey, mode.name).apply()
        _themeMode.value = mode
    }
    
    private fun getStoredThemeMode(): ThemeMode {
        val stored = prefs.getString(themeKey, ThemeMode.SYSTEM.name)
        return try {
            ThemeMode.valueOf(stored ?: ThemeMode.SYSTEM.name)
        } catch (e: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }
    }
}
