package com.goldmansachs.apod.data.api

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.goldmansachs.apod.data.model.ApodEntity

@Database(entities = [ApodEntity::class], version = 1)
abstract class DatabaseProvider : RoomDatabase() {
    companion object {
        private val TAG = DatabaseProvider::class.java.name

        private var instance: DatabaseProvider? = null

        @Synchronized
        fun getInstance(ctx: Context): DatabaseProvider {
            if(instance == null)
                instance = Room.databaseBuilder(ctx.applicationContext, DatabaseProvider::class.java,
                    "apod_database.db")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()

            return instance!!
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }

    abstract fun apodDao(): ApodDao
}