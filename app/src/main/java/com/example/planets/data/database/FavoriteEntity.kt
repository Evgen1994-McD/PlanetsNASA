package com.example.planets.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.planets.data.model.ApodItem

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdurl: String?,
    val mediaType: String,
    val serviceVersion: String,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toApodItem(): ApodItem {
        return ApodItem(
            date = date,
            title = title,
            explanation = explanation,
            url = url,
            hdurl = hdurl,
            mediaType = mediaType,
            serviceVersion = serviceVersion
        )
    }
}

fun ApodItem.toFavoriteEntity(): FavoriteEntity {
    return FavoriteEntity(
        date = date,
        title = title,
        explanation = explanation,
        url = url,
        hdurl = hdurl,
        mediaType = mediaType,
        serviceVersion = serviceVersion
    )
}
