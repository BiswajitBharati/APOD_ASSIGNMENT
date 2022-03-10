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

class MainViewModel(private val apodRepository: ApodRepository) : ViewModel() {
    companion object {
        private val TAG = MainViewModel::class.java.name
    }

    var jobNwAPOD: Job? = null
    var jobSaveAPOD: Job? = null
    var jobFavAPOD: Job? = null
    var jobLatestAPOD: Job? = null
    var jobDateAPOD: Job? = null

    private val _apodResponse = MutableLiveData<Pair<Boolean, ApodEntity?>>()
    val apodResponse: LiveData<Pair<Boolean, ApodEntity?>> = _apodResponse

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus

    private val _favStatus = MutableLiveData<Boolean>()
    val favStatus: LiveData<Boolean> = _favStatus

    private val _latestApod = MutableLiveData<ApodEntity?>()
    val latestApod: LiveData<ApodEntity?> = _latestApod

    private val _dateApod = MutableLiveData<ApodEntity?>()
    val dateApod: LiveData<ApodEntity?> = _dateApod

    fun getApodByDateFromNW(date: String) {
        Log.d(TAG, "getApodByDateFromNW: date: $date")
        jobNwAPOD = CoroutineScope(Dispatchers.IO).launch {
            val apodResponse = apodRepository.getApodByDateFromNW(date = date)

            Log.d(TAG, "getApodByDateFromNW: APOD Response Error Code: ${apodResponse?.code()} Message: ${apodResponse?.message()} Body: ${apodResponse?.body()}")
            _apodResponse.postValue(Pair(apodResponse?.isSuccessful ?: false, apodResponse?.body()))
        }
    }

    fun saveApodIntoDB(apodEntity: ApodEntity?) {
        Log.d(TAG, "saveApodIntoDB() == apodEntity: $apodEntity")
        jobSaveAPOD = CoroutineScope(Dispatchers.IO).launch {
            val isSuccess = apodRepository.saveApodIntoDB(apodEntity = apodEntity)
            _saveStatus.postValue(isSuccess)
        }
    }

    fun updateApodFavStatusIntoDB(isFavourite: Boolean, date: String) {
        Log.d(TAG, "updateApodFavStatusIntoDB() == isFavourite: $isFavourite date: $date")
        jobFavAPOD = CoroutineScope(Dispatchers.IO).launch {
            val isSuccess = apodRepository.updateApodFavStatusIntoDB(isFavourite = isFavourite, date = date)
            _favStatus.postValue(isSuccess)
        }
    }

    fun getLatestApodFromDB() {
        Log.d(TAG, "getLatestApodFromDB()")
        jobLatestAPOD = CoroutineScope(Dispatchers.IO).launch {
            val apodEntity = apodRepository.getLatestApodFromDB()
            _latestApod.postValue(apodEntity)
        }
    }

    fun getApodByDateFromDB(date: String) {
        Log.d(TAG, "getApodByDateFromDB() == date: $date")
        jobDateAPOD = CoroutineScope(Dispatchers.IO).launch {
            val apodEntity = apodRepository.getApodByDateFromDB(date = date)
            _dateApod.postValue(apodEntity)
        }
    }

    override fun onCleared() {
        super.onCleared()
        jobNwAPOD?.cancel()
        jobSaveAPOD?.cancel()
        jobFavAPOD?.cancel()
        jobLatestAPOD?.cancel()
        jobDateAPOD?.cancel()
    }
}