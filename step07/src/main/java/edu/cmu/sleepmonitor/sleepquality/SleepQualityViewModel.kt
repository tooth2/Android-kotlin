package edu.cmu.sleepmonitor.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.cmu.sleepmonitor.database.SleepDatabaseDao
import edu.cmu.sleepmonitor.database.SleepNight
import kotlinx.coroutines.*

class SleepQualityViewModel(
    private val sleepNightKey: Long = 0L,
    val database: SleepDatabaseDao) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope =  CoroutineScope(Dispatchers.Main + viewModelJob)
    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()

    val navigateToSleepTracker: LiveData<Boolean?> get() = _navigateToSleepTracker

    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun onSetSleepQuality(quality: Int) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val tonight = database.get(sleepNightKey) ?: return@withContext
                tonight.sleepQuality = quality
                database.update(tonight)
            }
            _navigateToSleepTracker.value = true
        }
    }


}