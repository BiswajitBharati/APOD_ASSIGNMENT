package com.goldmansachs.apod.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ApodEntity.TABLE_NAME)
data class ApodEntity(
    @ColumnInfo(name = _ID)
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,

    @ColumnInfo(name = COPYRIGHT)
    var copyright: String?,

    @ColumnInfo(name = DATE)
    var date: String?,

    @ColumnInfo(name = EXPLANATION)
    var explanation: String?,

    @ColumnInfo(name = HDURL)
    var hdurl: String?,

    @ColumnInfo(name = MEDIA_TYPE)
    var media_type: String?,

    @ColumnInfo(name = SERVICE_VERSION)
    var service_version: String?,

    @ColumnInfo(name = THUMB_URL)
    var thumbnail_url: String?,

    @ColumnInfo(name = TITLE)
    var title: String?,

    @ColumnInfo(name = URL)
    var url: String?,

    @ColumnInfo(name = IS_FAVOURITE)
    var is_favourite: Int = 0
) {
    companion object Contract {
        const val TABLE_NAME: String = "apod_nasa"

        const val _ID: String = "_id"

        const val COPYRIGHT: String = "copyright"

        const val DATE: String = "date"

        const val EXPLANATION: String = "explanation"

        const val HDURL: String = "hdurl"

        const val MEDIA_TYPE: String = "media_type"

        const val SERVICE_VERSION: String = "service_version"

        const val THUMB_URL: String = "thumbnail_url"

        const val TITLE: String = "title"

        const val URL: String = "url"

        const val IS_FAVOURITE: String = "is_favourite"
    }
}
