package com.goldmansachs.apod.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.goldmansachs.apod.data.model.ApodEntity
import com.goldmansachs.apod.data.repository.ApodRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ApodViewModel(private val apodRepository: ApodRepository) : ViewModel() {
    companion object {
        private val TAG = ApodViewModel::class.java.name
    }

    var jobUpdateFavAPOD: Job? = null
    var jobAllAPOD: Job? = null
    var jobFavAPOD: Job? = null

    private val _favStatus = MutableLiveData<Pair<Boolean, String>>()
    val favStatus: LiveData<Pair<Boolean, String>> = _favStatus

    private val _allApod = MutableLiveData<List<ApodEntity>?>()
    val allApod: LiveData<List<ApodEntity>?> = _allApod

    private val _favouriteApod = MutableLiveData<List<ApodEntity>?>()
    val favouriteApod: LiveData<List<ApodEntity>?> = _favouriteApod

    fun updateApodFavStatusIntoDB(isFavourite: Boolean, date: String) {
        Log.d(TAG, "updateApodFavStatusIntoDB() == isFavourite: $isFavourite date: $date")
        jobUpdateFavAPOD = CoroutineScope(Dispatchers.IO).launch {
            val isSuccess = apodRepository.updateApodFavStatusIntoDB(isFavourite = isFavourite, date = date)
            _favStatus.postValue(Pair(isSuccess, date))
        }
    }

    fun getAllApodFromDB() {
        Log.d(TAG, "getAllApodFromDB()")
        jobAllAPOD = CoroutineScope(Dispatchers.IO).launch {
            val allApodEntity = apodRepository.getAllApodFromDB()
            _allApod.postValue(allApodEntity)
        }
    }

    fun getAllApodByFavStatusFromDB(favStatus: Int) {
        Log.d(TAG, "getAllApodByFavStatusFromDB() == favStatus: $favStatus")
        jobFavAPOD = CoroutineScope(Dispatchers.IO).launch {
            val favApodEntity = apodRepository.getAllApodByFavStatusFromDB(favStatus)
            _favouriteApod.postValue(favApodEntity)
        }
    }

    override fun onCleared() {
        super.onCleared()
        jobUpdateFavAPOD?.cancel()
        jobAllAPOD?.cancel()
        jobFavAPOD?.cancel()
    }
}