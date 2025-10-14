package com.example.planets.di

import com.example.planets.domain.repository.ApodRepository
import com.example.planets.domain.repository.ThemeRepository
import com.example.planets.domain.usecase.ClearCacheUseCase
import com.example.planets.domain.usecase.ClearCacheUseCaseImpl
import com.example.planets.domain.usecase.GetApodDetailUseCase
import com.example.planets.domain.usecase.GetApodDetailUseCaseImpl
import com.example.planets.domain.usecase.GetApodListUseCase
import com.example.planets.domain.usecase.GetApodListUseCaseImpl
import com.example.planets.domain.usecase.GetFavoritesUseCase
import com.example.planets.domain.usecase.GetFavoritesUseCaseImpl
import com.example.planets.domain.usecase.IsFavoriteUseCase
import com.example.planets.domain.usecase.IsFavoriteUseCaseImpl
import com.example.planets.domain.usecase.NotifyCacheClearedUseCase
import com.example.planets.domain.usecase.NotifyCacheClearedUseCaseImpl
import com.example.planets.domain.usecase.ThemeUseCase
import com.example.planets.domain.usecase.ThemeUseCaseImpl
import com.example.planets.domain.usecase.ToggleFavoriteUseCase
import com.example.planets.domain.usecase.ToggleFavoriteUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    @Singleton
    fun provideGetApodListUseCase(repository: ApodRepository): GetApodListUseCase {
        return GetApodListUseCaseImpl(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetApodDetailUseCase(repository: ApodRepository): GetApodDetailUseCase {
        return GetApodDetailUseCaseImpl(repository)
    }
    
    @Provides
    @Singleton
    fun provideToggleFavoriteUseCase(repository: ApodRepository): ToggleFavoriteUseCase {
        return ToggleFavoriteUseCaseImpl(repository)
    }
    
    @Provides
    @Singleton
    fun provideIsFavoriteUseCase(repository: ApodRepository): IsFavoriteUseCase {
        return IsFavoriteUseCaseImpl(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetFavoritesUseCase(repository: ApodRepository): GetFavoritesUseCase {
        return GetFavoritesUseCaseImpl(repository)
    }
    
    @Provides
    @Singleton
    fun provideClearCacheUseCase(
        repository: ApodRepository,
        notifyCacheClearedUseCase: NotifyCacheClearedUseCase
    ): ClearCacheUseCase {
        return ClearCacheUseCaseImpl(repository, notifyCacheClearedUseCase)
    }
    
    @Provides
    @Singleton
    fun provideNotifyCacheClearedUseCase(): NotifyCacheClearedUseCase {
        return NotifyCacheClearedUseCaseImpl()
    }
    
    @Provides
    @Singleton
    fun provideThemeUseCase(themeRepository: ThemeRepository): ThemeUseCase {
        return ThemeUseCaseImpl(themeRepository)
    }
}
