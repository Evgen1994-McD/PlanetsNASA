package com.example.planets.di

import com.example.planets.domain.usecase.ClearCacheUseCase
import com.example.planets.domain.usecase.ClearCacheUseCaseImpl
import com.example.planets.domain.usecase.GetApodDetailUseCase
import com.example.planets.domain.usecase.GetApodDetailUseCaseImpl
import com.example.planets.domain.usecase.GetApodListUseCase
import com.example.planets.domain.usecase.GetApodListUseCaseImpl
import com.example.planets.domain.usecase.GetFavoritesUseCase
import com.example.planets.domain.usecase.GetFavoritesUseCaseImpl
import com.example.planets.domain.usecase.InvalidatePagingSourceUseCase
import com.example.planets.domain.usecase.InvalidatePagingSourceUseCaseImpl
import com.example.planets.domain.usecase.IsFavoriteUseCase
import com.example.planets.domain.usecase.IsFavoriteUseCaseImpl
import com.example.planets.domain.usecase.NotifyCacheClearedUseCase
import com.example.planets.domain.usecase.NotifyCacheClearedUseCaseImpl
import com.example.planets.domain.usecase.ThemeUseCase
import com.example.planets.domain.usecase.ThemeUseCaseImpl
import com.example.planets.domain.usecase.ToggleFavoriteUseCase
import com.example.planets.domain.usecase.ToggleFavoriteUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindGetApodListUseCase(
        impl: GetApodListUseCaseImpl
    ): GetApodListUseCase

    @Binds
    abstract fun bindGetApodDetailUseCase(
        impl: GetApodDetailUseCaseImpl
    ): GetApodDetailUseCase

    @Binds
    abstract fun bindToggleFavoriteUseCase(
        impl: ToggleFavoriteUseCaseImpl
    ): ToggleFavoriteUseCase

    @Binds
    abstract fun bindIsFavoriteUseCase(
        impl: IsFavoriteUseCaseImpl
    ): IsFavoriteUseCase

    @Binds
    abstract fun bindGetFavoritesUseCase(
        impl: GetFavoritesUseCaseImpl
    ): GetFavoritesUseCase

    @Binds
    abstract fun bindClearCacheUseCase(
        impl: ClearCacheUseCaseImpl
    ): ClearCacheUseCase

    @Binds
    abstract fun bindNotifyCacheClearedUseCase(
        impl: NotifyCacheClearedUseCaseImpl
    ): NotifyCacheClearedUseCase

    @Binds
    abstract fun bindInvalidatePagingSourceUseCase(
        impl: InvalidatePagingSourceUseCaseImpl
    ): InvalidatePagingSourceUseCase

    @Binds
    abstract fun bindThemeUseCase(
        impl: ThemeUseCaseImpl
    ): ThemeUseCase
}