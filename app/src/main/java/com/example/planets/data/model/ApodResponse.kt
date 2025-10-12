package com.example.planets.data.model

import com.google.gson.annotations.SerializedName

data class ApodResponse(
    @SerializedName("id")
    val id: Int = 0,
    
    @SerializedName("date")
    val date: String?,
    
    @SerializedName("explanation")
    val explanation: String?,
    
    @SerializedName("hdurl")
    val hdurl: String?,
    
    @SerializedName("media_type")
    val mediaType: String?,
    
    @SerializedName("service_version")
    val serviceVersion: String?,
    
    @SerializedName("title")
    val title: String?,
    
    @SerializedName("url")
    val url: String?
)
