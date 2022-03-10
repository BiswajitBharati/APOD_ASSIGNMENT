package com.goldmansachs.apod.data.api

import androidx.room.*
import com.goldmansachs.apod.data.model.ApodEntity

@Dao
interface ApodDao {
    @Insert
    suspend fun insert(apodEntity: ApodEntity) : Long

    @Update
    suspend fun update(apodEntity: ApodEntity) : Int

    @Delete
    suspend fun delete(apodEntity: ApodEntity) : Int

    @Query("DELETE FROM ${ApodEntity.TABLE_NAME}")
    suspend fun deleteAll() : Int

    @Transaction
    @Query("SELECT * FROM ${ApodEntity.TABLE_NAME}")
    suspend fun getAllApod(): List<ApodEntity>?

    @Transaction
    @Query("SELECT * FROM ${ApodEntity.TABLE_NAME} WHERE ${ApodEntity._ID}= :id")
    suspend fun getApodById(id : Int): ApodEntity?

    @Transaction
    @Query("SELECT * FROM ${ApodEntity.TABLE_NAME} WHERE ${ApodEntity.DATE}= :date")
    suspend fun getApodByDate(date : String): ApodEntity?

    @Transaction
    @Query("SELECT * FROM ${ApodEntity.TABLE_NAME} WHERE ${ApodEntity.IS_FAVOURITE}= :status")
    suspend fun getAllApodByFavStatus(status : Int): List<ApodEntity>?

    @Transaction
    @Query("SELECT * FROM ${ApodEntity.TABLE_NAME} ORDER BY ${ApodEntity._ID} DESC LIMIT 1")
    suspend fun getLatestApod(): ApodEntity?
}