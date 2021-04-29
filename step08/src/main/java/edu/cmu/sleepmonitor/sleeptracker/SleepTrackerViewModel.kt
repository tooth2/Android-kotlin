package edu.cmu.sleepmonitor.sleeptracker

import android.app.Application
import androidx.lifecycle.*
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

        //viewModelJob allows us to cancel all coroutines started by this ViewModel.
        //private var viewModelJob = Job()
        //coroutine scope
        //private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob )
        private var tonight = MutableLiveData<SleepNight?>()
        var nights = database.getAllNights()

        /**
         * Converted nights to Spanned for displaying.
         */
        val nightsString = Transformations.map(nights) { nights ->
                formatNights(nights, application.resources)
        }

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

        private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
        val navigateToSleepQuality : LiveData<SleepNight>
                get() = _navigateToSleepQuality

        fun doneShowingSnackbar() {
                _showSnackbarEvent.value = false
        }

        fun doneNavigating(){
                _navigateToSleepQuality.value = null
        }

        private val _navigateToSleepDataQuality = MutableLiveData<Long>()
        val navigateToSleepDataQuality
                get() = _navigateToSleepDataQuality

        fun onSleepNightClicked(id: Long) {
                _navigateToSleepDataQuality.value = id
        }
        fun onSleepDataQualityNavigated() {
                _navigateToSleepDataQuality.value = null
        }

        init{
                initializeTonight()
        }

        private fun initializeTonight() {
               // uiScope.launch {
                viewModelScope.launch{
                        tonight.value= getTonightFromDatabase()
                }
        }

        private suspend fun getTonightFromDatabase(): SleepNight? {
                //return withContext(Dispatchers.IO) {
                        var night = database.getTonight()
                        if (night?.endTimeMilli != night?.startTimeMilli) {
                                night = null
                        }
                        return night
                //}
        }
        private suspend fun insert(night: SleepNight) {
                withContext(Dispatchers.IO) {
                        database.insert(night)
                }
        }

        private suspend fun update(night: SleepNight) {
                withContext(Dispatchers.IO) {
                        database.update(night)
                }
        }

        private suspend fun clear() {
                withContext(Dispatchers.IO) {
                        database.clear()
                }
        }

        /**
         * Executes when the START button is clicked.
         */
        fun onStartTracking() {
                // crate a new night, capturing the current time
                //uiScope.launch {
                viewModelScope.launch{
                        val newNight = SleepNight()
                        insert(newNight)
                        tonight.value = getTonightFromDatabase()
                }
        }

        /**
         * Executes when the STOP button is clicked.
         */
        fun onStopTracking() {
                //uiScope.launch {
                viewModelScope.launch{
                        val oldNight = tonight.value ?: return@launch
                        oldNight.endTimeMilli = System.currentTimeMillis()
                        update(oldNight)
                        _navigateToSleepQuality.value = oldNight
                }
        }

        /**
         * Executes when the CLEAR button is clicked.
         */
        fun onClear() {
                //uiScope.launch {
                viewModelScope.launch{
                        clear()
                        tonight.value = null
                }
                _showSnackbarEvent.value = true  //addind snack bar event
        }


        /**
         * Called when the ViewModel is dismantled.
         * At this point, we want to cancel all coroutines;
         * otherwise we end up with processes that have nowhere to return to
         * using memory and resources.
        override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        }
         */
}

