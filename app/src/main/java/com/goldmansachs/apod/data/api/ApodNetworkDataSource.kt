package com.goldmansachs.apod.data.api

import android.util.Log
import com.goldmansachs.apod.data.model.ApodEntity
import retrofit2.Response

class ApodNetworkDataSource(private val apodService: ApodService) {
    companion object {
        private val TAG = ApodNetworkDataSource::class.java.name
    }

    suspend fun getApodByDate(date: String): Response<ApodEntity>? {
        Log.d(TAG, "getApodByDate: date: $date")
        return try {
            apodService.getApodByDate(date = date)
        } catch (error: Exception) {
            Log.e(TAG, "Error: ${error.message}")
            null
        }
    }
}