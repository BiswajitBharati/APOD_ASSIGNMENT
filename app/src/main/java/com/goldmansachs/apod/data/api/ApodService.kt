package com.goldmansachs.apod.data.api

import com.goldmansachs.apod.data.model.ApodEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApodService {

    @GET("planetary/apod?api_key=DEMO_KEY&thumbs=true")
    suspend fun getApodByDate(@Query("date") date: String): Response<ApodEntity>?
}