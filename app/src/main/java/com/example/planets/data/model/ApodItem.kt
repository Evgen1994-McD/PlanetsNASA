package com.example.planets.data.model

data class ApodItem(
    val id: Int = 0,
    val date: String,
    val explanation: String,
    val hdurl: String?,
    val mediaType: String,
    val serviceVersion: String,
    val title: String,
    val url: String
)

fun ApodResponse.toApodItem(): ApodItem {
    return ApodItem(
        id = this.id,
        date = this.date ?: "",
        explanation = this.explanation ?: "",
        hdurl = this.hdurl,
        mediaType = this.mediaType ?: "image",
        serviceVersion = this.serviceVersion ?: "v1",
        title = this.title ?: "",
        url = this.url ?: ""
    )
}
