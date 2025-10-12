package com.example.planets.data.api

import com.example.planets.data.model.ApodResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    
    @GET("planetary/apod")
    suspend fun getApod(
        @Query("api_key") apiKey: String,
        @Query("date") date: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("count") count: Int? = null,
        @Query("thumbs") thumbs: Boolean? = null
    ): Response<ApodResponse>
    
    @GET("planetary/apod")
    suspend fun getApodList(
        @Query("api_key") apiKey: String,
        @Query("count") count: Int = 10
    ): Response<List<ApodResponse>>
}
