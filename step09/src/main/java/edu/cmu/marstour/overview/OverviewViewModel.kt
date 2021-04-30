/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package edu.cmu.marstour.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cmu.marstour.network.MarsApi
import edu.cmu.marstour.network.MarsApiFilter
import edu.cmu.marstour.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
enum class MarsApiStatus { LOADING, ERROR, DONE }

class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the status of the most recent request
    //type change : String --> MarsApiStatus
    private val _status = MutableLiveData<MarsApiStatus>()
    val status: LiveData<MarsApiStatus>
        get() = _status
    // The external immutable LiveData for the request status String

    private val _properties = MutableLiveData<List<MarsProperty>>()
    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()
    val navigateToSelectedProperty: LiveData<MarsProperty>
        get() = _navigateToSelectedProperty
    // private var viewModelJob = Job()
    // private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )
    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }

    /**
     * Sets the value of the response LiveData to the Mars API status or the successful number of
     * Mars properties retrieved.
     */
    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
        //coroutineScope.launch {
        viewModelScope.launch {
            //var getPropertiesDeferred = MarsApi.retrofitService.getProperties(filter.value)
            //var getPropertiesDeferred = MarsApi.retrofitService.getProperties()
            _status.value = MarsApiStatus.LOADING
            try {
                //var listResult = getPropertiesDeferred.await()
               // var listResult = MarsApi.retrofitService.getProperties()
                //_status.value = "Success: ${listResult.size}"
                _properties.value = MarsApi.retrofitService.getProperties(filter.value)
                _status.value = MarsApiStatus.DONE
                //if (listResult.size > 0) {
                //    _properties.value = listResult
                //}
                //_status.value = "Success: ${listResult.size} Mars properties retrieved"
            } catch (e: Exception) {
                //_status.value = "Failure: ${e.message}"
                _status.value = MarsApiStatus.ERROR
                _properties.value = ArrayList()
            }
            /***
            MarsApi.retrofitService.getProperties().enqueue( object: Callback<List<MarsProperty>> {
            override fun onFailure(call: Call<List<MarsProperty>>, t: Throwable) {
                //_response.value = "Set the Mars API Response here!"
                _response.value = "Failure: " + t.message
            }

            override fun onResponse(call: Call<List<MarsProperty>>, response: Response<List<MarsProperty>>) {
                _response.value = "Success: ${response.body()?.size} Mars properties retrieved"
            }
            ***/
        }
    }
    fun updateFilter(filter: MarsApiFilter) {
        getMarsRealEstateProperties(filter)
    }
    fun displayPropertyDetails(marsProperty: MarsProperty) {
        _navigateToSelectedProperty.value = marsProperty
    }
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }
    /***
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
    ***/
}
