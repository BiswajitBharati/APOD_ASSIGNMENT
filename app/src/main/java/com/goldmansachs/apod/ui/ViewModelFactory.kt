package com.goldmansachs.apod.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.goldmansachs.apod.data.api.ApodLocalDataSource
import com.goldmansachs.apod.data.api.ApodNetworkDataSource
import com.goldmansachs.apod.data.api.ApodService
import com.goldmansachs.apod.data.repository.ApodRepository

class ViewModelFactory(private val application: Application, private val apodService: ApodService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apodRepository = ApodRepository(
            apodNetworkDataSource = ApodNetworkDataSource(apodService = apodService),
            apodLocalDataSource = ApodLocalDataSource(application = application))
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(apodRepository = apodRepository) as T
        } else if (modelClass.isAssignableFrom(ApodViewModel::class.java)) {
            return ApodViewModel(apodRepository = apodRepository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}