package edu.cmu.sleepmonitor.sleepdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.cmu.sleepmonitor.database.SleepDatabaseDao

/**
 * Provides the key for the night and the SleepDatabaseDao to the ViewModel.
 */
class SleepDetailViewModelFactory(
    private val sleepNightKey: Long,
    private val dataSource: SleepDatabaseDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepDetailViewModel::class.java)) {
            return SleepDetailViewModel(sleepNightKey, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}