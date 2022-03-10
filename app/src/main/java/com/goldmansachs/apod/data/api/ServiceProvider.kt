package com.goldmansachs.apod.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceProvider {

    private const val BASE_URL = "https://api.nasa.gov/"

    var client: OkHttpClient = OkHttpClient.Builder().build()

    private var retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(client).build()

    fun <T> createService(serviceClass: Class<T>): T = retrofit.create(serviceClass)
}