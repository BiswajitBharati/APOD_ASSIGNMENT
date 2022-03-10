package com.goldmansachs.apod.data.repository

import android.util.Log
import com.goldmansachs.apod.data.api.ApodLocalDataSource
import com.goldmansachs.apod.data.api.ApodNetworkDataSource
import com.goldmansachs.apod.data.model.ApodEntity
import retrofit2.Response

class ApodRepository(private val apodNetworkDataSource: ApodNetworkDataSource, private val apodLocalDataSource: ApodLocalDataSource) {
    companion object {
        private val TAG = ApodRepository::class.java.name
    }

    suspend fun getApodByDateFromNW(date: String): Response<ApodEntity>? {
        Log.d(TAG, "getApodByDateFromNW: date: $date")
        return apodNetworkDataSource.getApodByDate(date = date)
    }

    suspend fun saveApodIntoDB(apodEntity: ApodEntity?): Boolean {
        Log.d(TAG, "saveApodIntoDB() == apodEntity: $apodEntity")
        return apodLocalDataSource.saveApod(apodEntity = apodEntity)
    }

    suspend fun updateApodFavStatusIntoDB(isFavourite: Boolean, date: String): Boolean {
        Log.d(TAG, "updateApodFavStatusIntoDB() == isFavourite: $isFavourite date: $date")
        return apodLocalDataSource.updateApodFavStatus(isFavourite = isFavourite, date = date)
    }

    suspend fun getLatestApodFromDB(): ApodEntity? {
        Log.d(TAG, "getLatestApodFromDB()")
        return apodLocalDataSource.getLatestApod()
    }

    suspend fun getApodByDateFromDB(date: String): ApodEntity? {
        Log.d(TAG, "getApodByDateFromDB() == date: $date")
        return apodLocalDataSource.getApodByDate(date = date)
    }

    suspend fun getAllApodFromDB(): List<ApodEntity>? {
        Log.d(TAG, "getAllApodFromDB()")
        return apodLocalDataSource.getAllApod()
    }

    suspend fun getAllApodByFavStatusFromDB(favStatus: Int): List<ApodEntity>? {
        Log.d(TAG, "getAllApodByFavStatusFromDB() == favStatus: $favStatus")
        return apodLocalDataSource.getAllApodByFavStatus(favStatus)
    }
}