package com.example.planets.di

import com.example.planets.domain.repository.ApodRepository
import com.example.planets.domain.usecase.ClearCacheUseCase
import com.example.planets.domain.usecase.GetApodDetailUseCase
import com.example.planets.domain.usecase.GetApodListUseCase
import com.example.planets.domain.usecase.GetFavoritesUseCase
import com.example.planets.domain.usecase.IsFavoriteUseCase
import com.example.planets.domain.usecase.ToggleFavoriteUseCase
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
        return GetApodListUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetApodDetailUseCase(repository: ApodRepository): GetApodDetailUseCase {
        return GetApodDetailUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideToggleFavoriteUseCase(repository: ApodRepository): ToggleFavoriteUseCase {
        return ToggleFavoriteUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideIsFavoriteUseCase(repository: ApodRepository): IsFavoriteUseCase {
        return IsFavoriteUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetFavoritesUseCase(repository: ApodRepository): GetFavoritesUseCase {
        return GetFavoritesUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideClearCacheUseCase(repository: ApodRepository): ClearCacheUseCase {
        return ClearCacheUseCase(repository)
    }
}
