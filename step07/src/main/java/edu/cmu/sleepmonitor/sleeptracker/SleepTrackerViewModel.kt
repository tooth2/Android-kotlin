package edu.cmu.sleepmonitor.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import edu.cmu.sleepmonitor.database.SleepDatabaseDao
import edu.cmu.sleepmonitor.database.SleepNight
import edu.cmu.sleepmonitor.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {
        private var viewModelJob = Job()

        override fun onCleared(){
                super.onCleared()
                viewModelJob.cancel()
        }
        //coroutine scope
        private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob )
        private var tonight = MutableLiveData<SleepNight?>()
        private var nights = database.getAllNights()

        val startButtonVisible = Transformations.map(tonight) {
                null == it
        }
        val stopButtonVisible = Transformations.map(tonight) {
                null != it
        }
        val clearButtonVisible = Transformations.map(nights) {
                it?.isNotEmpty()
        }

        private var _showSnackbarEvent = MutableLiveData<Boolean>()

        val showSnackBarEvent: LiveData<Boolean>
                get() = _showSnackbarEvent

        fun doneShowingSnackbar() {
                _showSnackbarEvent.value = false
        }
        //transformation map
        val nightsString = Transformations.map(nights) { nights ->
                formatNights(nights, application.resources)
        }

        private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
        val navigateToSleepQuality : LiveData<SleepNight>
                get() = _navigateToSleepQuality
        fun doneNavigating(){
                _navigateToSleepQuality.value = null
        }
        init{
                initializeTonight()
        }

        private fun initializeTonight() {
                uiScope.launch {
                        tonight.value= getTonightFromDatabase()
                }
        }

        private suspend fun getTonightFromDatabase(): SleepNight? {
                return withContext(Dispatchers.IO) {
                        var night = database.getTonight()
                        if (night?.endTimeMilli != night?.startTimeMilli) {
                                night = null
                        }
                        night
                }

        }
        fun onStartTracking() {
                // crate a new night, capturing the current time
                uiScope.launch {
                        val newNight = SleepNight()
                        insert(newNight)
                        tonight.value = getTonightFromDatabase()
                }
        }

        private suspend fun insert(night: SleepNight) {
                withContext(Dispatchers.IO) {
                        database.insert(night)
                }
        }

        fun onStopTracking() {
                uiScope.launch {

                        val oldNight = tonight.value ?: return@launch
                        oldNight.endTimeMilli = System.currentTimeMillis()
                        update(oldNight)
                        _navigateToSleepQuality.value = oldNight
                }
        }
        private suspend fun update(night: SleepNight) {
                withContext(Dispatchers.IO) {
                        database.update(night)
                }
        }

        fun onClear() {
                uiScope.launch {
                        clear()
                        tonight.value = null
                        _showSnackbarEvent.value = true  //addind snack bar event
                }
        }

        suspend fun clear() {
                withContext(Dispatchers.IO) {
                        database.clear()
                }
        }


}

