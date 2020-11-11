package com.emilio.android.ufceventrecap.detail

import android.app.Application
import androidx.lifecycle.*
import com.emilio.android.ufceventrecap.database.getDatabase
import com.emilio.android.ufceventrecap.domain.DevByteEvent
import com.emilio.android.ufceventrecap.repository.EventsRepository
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException

    /**
     * EventDetailViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
     * allows data to survive configuration changes such as screen rotations. In addition, background
     * work such as fetching network results can continue through configuration changes and deliver
     * results after the new Fragment or Activity is available.
     *
     * @param application The application that this viewmodel is attached to, it's safe to hold a
     * reference to applications across rotation since Application is never recreated during actiivty
     * or fragment lifecycle events.
     */
    open class EventDetailViewModel(eventSelected: DevByteEvent, application: Application) : AndroidViewModel(application) {
        private var firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(application)

        // The internal MutableLiveData for the selected property
        private val _selectedUFCEvent = MutableLiveData<DevByteEvent>()

        // The external LiveData for the SelectedUFEvent
        val selectedUFEvent: LiveData<DevByteEvent>
            get() = _selectedUFCEvent

        /**
         * The data source this ViewModel will fetch results from.
         */
        private val eventsRepository = EventsRepository(getDatabase(application))

        /**
         * A playlist of UFC Events displayed on the screen.
         */
        val playlist = eventsRepository.events

        /**
         * Called from "EventDetailViewModelTest"
         */
        open val myEvents: LiveData<List<DevByteEvent>> = Transformations.map(eventsRepository.events)  { playlist ->
            if (playlist.isNullOrEmpty()) {
                refreshDataFromRepository()
            } else {
                _eventNetworkError.value = true
            }
            return@map playlist
        }

        /**
         * This is the job for all coroutines started by this ViewModel.
         *
         * Cancelling this job will cancel all coroutines started by this ViewModel.
         */
        private val viewModelJob = SupervisorJob()

        /**
         * This is the main scope for all coroutines launched by MainViewModel.
         *
         * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
         * viewModelJob.cancel()
         */
        private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

        /**
         * Event triggered for network error. This is private to avoid exposing a
         * way to set this value to observers.
         */
        private var _eventNetworkError = MutableLiveData<Boolean>(false)

        /**
         * Event triggered for network error. Views should use this to get access
         * to the data.
         */
        val eventNetworkError: LiveData<Boolean>
            get() = _eventNetworkError


        /**
         * Flag to display the error message. This is private to avoid exposing a
         * way to set this value to observers.
         */
        private var _isNetworkErrorShown = MutableLiveData(false)

        /**
         * Flag to display the error message. Views should use this to get access
         * to the data.
         */
        val isNetworkErrorShown: LiveData<Boolean>
            get() = _isNetworkErrorShown

        /**
         * init{} is called immediately when this ViewModel is created.
         * Method "refreshDataFromRepository()" is prefixed as 'open' to allow for access
         * from JUnit test.
         */
        init {
            _selectedUFCEvent.value = eventSelected
            // Please read Description above.
            refreshDataFromRepository()
        }

        /**
         * Refresh data from the repository. Use a coroutine launch to run in a
         * background thread. This Method is prefixed as 'open' to allow for access
         * from JUnit test.
         */
        open fun refreshDataFromRepository() {
            viewModelScope.launch {
                try {eventsRepository.refreshUFCEvents()
                    firebaseAnalytics.setUserProperty("AccessWebServiceProperty", "Playlist of UFC Events downloaded.")
                    firebaseAnalytics.setUserProperty("UFCEventsCached", "Latest UFC Events cached.")
                    _eventNetworkError.value = false
                    _isNetworkErrorShown.value = false

                } catch (networkError: IOException) {
                    // Show a Toast error message and hide the progress bar.
                    if(playlist.value.isNullOrEmpty())
                        _eventNetworkError.value = true
                }
            }
        }

        /**
         * Resets the network error flag.
         */
        fun onNetworkErrorShown() {
            _isNetworkErrorShown.value = true
        }

        /**
         * Cancel all coroutines when the ViewModel is cleared
         */
        override fun onCleared() {
            super.onCleared()
            viewModelJob.cancel()
        }

        /**
         * Factory for constructing EventDetailViewModel with parameter
         */
        class Factory(val event: DevByteEvent, val app: Application) : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(EventDetailViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return EventDetailViewModel(event, app) as T
                }
                throw IllegalArgumentException("Unable to construct viewmodel")
            }
        }

        open fun isPlaylistEmpty() : Boolean? {
            _eventNetworkError.value = false
            if (playlist.value.isNullOrEmpty()) {
                _eventNetworkError.value = true
            }
            return eventNetworkError.value
        }
    }