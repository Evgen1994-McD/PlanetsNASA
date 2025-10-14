package com.example.planets.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {
    
    // APOD Cache methods
    @Query("SELECT * FROM apod_cache WHERE date = :date")
    suspend fun getApodByDate(date: String): ApodEntity?
    
    @Query("SELECT * FROM apod_cache ORDER BY cachedAt DESC LIMIT :limit")
    suspend fun getRecentCachedApods(limit: Int): List<ApodEntity>
    
    @Query("SELECT * FROM apod_cache ORDER BY cachedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getRecentCachedApods(limit: Int, offset: Int): List<ApodEntity>
    
    @Query("SELECT COUNT(*) FROM apod_cache")
    suspend fun getTotalCachedCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApod(apod: ApodEntity)
    
    @Query("DELETE FROM apod_cache WHERE cachedAt < :timestamp")
    suspend fun deleteOldApods(timestamp: Long)
    
    @Query("DELETE FROM apod_cache")
    suspend fun deleteAllApods()
    
    // Favorites methods
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)
    
    @Query("DELETE FROM favorites WHERE date = :date")
    suspend fun deleteFavoriteByDate(date: String)
    
    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE date = :date)")
    suspend fun isFavorite(date: String): Boolean
}
