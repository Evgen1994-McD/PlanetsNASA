package com.example.planets.di

import android.content.Context
import com.example.planets.data.api.ApiClient
import com.example.planets.data.api.NasaApiService
import com.example.planets.data.database.ApodDatabase
import com.example.planets.data.database.ApodDao
import com.example.planets.data.repository.ApodRepositoryImpl
import com.example.planets.domain.repository.ApodRepository
import com.example.planets.utils.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideNasaApiService(): NasaApiService {
        return ApiClient.nasaApiService
    }
    
    @Provides
    @Singleton
    fun provideApodDatabase(@ApplicationContext context: Context): ApodDatabase {
        return ApodDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideApodDao(database: ApodDatabase): ApodDao {
        return database.apodDao()
    }
    
    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }
    
    @Provides
    @Singleton
    fun provideApodRepository(
        apiService: NasaApiService,
        apodDao: ApodDao,
        networkMonitor: NetworkMonitor,
        @ApplicationContext context: Context
    ): ApodRepository {
        return ApodRepositoryImpl(apiService, apodDao, networkMonitor, context)
    }
}
