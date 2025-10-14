package com.example.planets.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apod_cache")
data class ApodEntity(
    @PrimaryKey
    val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdurl: String?,
    val mediaType: String,
    val serviceVersion: String,
    val cachedAt: Long = System.currentTimeMillis()
)
