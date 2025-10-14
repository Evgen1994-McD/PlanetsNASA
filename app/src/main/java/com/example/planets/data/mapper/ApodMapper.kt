package com.example.planets.data.mapper

import com.example.planets.data.database.ApodEntity
import com.example.planets.data.database.FavoriteEntity
import com.example.planets.data.model.ApodResponse
import com.example.planets.domain.model.Apod

/**
 * Маппер для преобразования между Domain и Data моделями
 */
object ApodMapper {
    
    /**
     * Преобразует ApodResponse (API) в Domain модель Apod
     */
    fun ApodResponse.toDomain(): Apod {
        return Apod(
            date = this.date ?: "",
            title = this.title ?: "",
            explanation = this.explanation ?: "",
            url = this.url ?: "",
            hdUrl = this.hdurl,
            mediaType = this.mediaType ?: "image",
            serviceVersion = this.serviceVersion ?: "v1"
        )
    }
    
    /**
     * Преобразует ApodEntity (Database) в Domain модель Apod
     */
    fun ApodEntity.toDomain(): Apod {
        return Apod(
            date = this.date,
            title = this.title,
            explanation = this.explanation,
            url = this.url,
            hdUrl = this.hdurl,
            mediaType = this.mediaType,
            serviceVersion = this.serviceVersion
        )
    }
    
    /**
     * Преобразует Domain модель Apod в ApodEntity (Database)
     */
    fun Apod.toEntity(): ApodEntity {
        return ApodEntity(
            date = this.date,
            title = this.title,
            explanation = this.explanation,
            url = this.url,
            hdurl = this.hdUrl,
            mediaType = this.mediaType,
            serviceVersion = this.serviceVersion
        )
    }
    
    /**
     * Преобразует Domain модель Apod в FavoriteEntity (Database)
     */
    fun Apod.toFavoriteEntity(): FavoriteEntity {
        return FavoriteEntity(
            date = this.date,
            title = this.title,
            explanation = this.explanation,
            url = this.url,
            hdurl = this.hdUrl,
            mediaType = this.mediaType,
            serviceVersion = this.serviceVersion
        )
    }
    
    /**
     * Преобразует FavoriteEntity (Database) в Domain модель Apod
     */
    fun FavoriteEntity.toDomain(): Apod {
        return Apod(
            date = this.date,
            title = this.title,
            explanation = this.explanation,
            url = this.url,
            hdUrl = this.hdurl,
            mediaType = this.mediaType,
            serviceVersion = this.serviceVersion
        )
    }
}
