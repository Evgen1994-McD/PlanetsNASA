package com.example.planets.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {
    
    @Query("SELECT * FROM apod_cache ORDER BY cachedAt DESC")
    fun getAllCachedApods(): Flow<List<ApodEntity>>
    
    @Query("SELECT * FROM apod_cache WHERE date = :date")
    suspend fun getApodByDate(date: String): ApodEntity?
    
    @Query("SELECT * FROM apod_cache ORDER BY cachedAt DESC LIMIT :limit")
    suspend fun getRecentCachedApods(limit: Int): List<ApodEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApod(apod: ApodEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApods(apods: List<ApodEntity>)
    
    @Delete
    suspend fun deleteApod(apod: ApodEntity)
    
    @Query("DELETE FROM apod_cache WHERE cachedAt < :timestamp")
    suspend fun deleteOldApods(timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM apod_cache")
    suspend fun getCachedApodsCount(): Int
}
