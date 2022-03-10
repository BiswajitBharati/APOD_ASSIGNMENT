package com.goldmansachs.apod.data.api

import android.app.Application
import android.util.Log
import com.goldmansachs.apod.data.model.ApodEntity

class ApodLocalDataSource(val application: Application) {
    companion object {
        private val TAG = ApodLocalDataSource::class.java.name
    }

    private var apodDao: ApodDao

    private val database = DatabaseProvider.getInstance(application)

    init {
        apodDao = database.apodDao()
    }

    suspend fun saveApod(apodEntity: ApodEntity?): Boolean {
        Log.d(TAG, "saveApod() == apodEntity: $apodEntity")
        return apodEntity?.let { apod ->
            apod.date?.let { date ->
                apodDao.getApodByDate(date = date)?.let {
                    apod.id = it.id
                    apod.is_favourite = it.is_favourite
                    apodDao.update(apod) > 0
                } ?: run {
                    apodDao.insert(apod) > 0
                }
            }
        } ?: run { false }
    }

    suspend fun updateApodFavStatus(isFavourite: Boolean, date: String): Boolean {
        Log.d(TAG, "updateApodFavStatus() == isFavourite: $isFavourite")
        return apodDao.getApodByDate(date = date)?.let {
            it.is_favourite = if (isFavourite) 1 else 0
            apodDao.update(it) > 0
        } ?: run { false }
    }

    suspend fun getLatestApod(): ApodEntity? {
        Log.d(TAG, "getLatestApod()")
        return apodDao.getLatestApod()
    }

    suspend fun getApodByDate(date: String): ApodEntity? {
        Log.d(TAG, "getApodByDate() == date: $date")
        return apodDao.getApodByDate(date = date)
    }

    suspend fun getAllApod(): List<ApodEntity>? {
        Log.d(TAG, "getAllApod()")
        return apodDao.getAllApod()
    }

    suspend fun getAllApodByFavStatus(favStatus: Int): List<ApodEntity>? {
        Log.d(TAG, "getAllApodByFavStatus() == favStatus: $favStatus")
        return apodDao.getAllApodByFavStatus(favStatus)
    }
}