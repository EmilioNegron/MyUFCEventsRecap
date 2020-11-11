/*
 * Copyright (C) 2019 Google Inc.
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
 */

package com.emilio.android.ufceventrecap.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.emilio.android.ufceventrecap.database.EventsDatabase
import com.emilio.android.ufceventrecap.database.asDomainModel
import com.emilio.android.ufceventrecap.domain.DevByteEvent
import com.emilio.android.ufceventrecap.network.DevByteNetwork
import com.emilio.android.ufceventrecap.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Repository for fetching events from the network and storing them on disk
 */
open class EventsRepository(private val database: EventsDatabase) {

    /**
     * Collection of UFC Events from our cache on disk, if available. Will be called from "refreshUFCEvents()" below.
     */
    open val events: LiveData<List<DevByteEvent>> = Transformations.map(database.eventDao.getEvents()) {
        it.asDomainModel()
    }

    /**
     * Refresh the events stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     */
    open suspend fun refreshUFCEvents() {
        withContext(Dispatchers.IO) {
            Timber.d("refresh UFC Events is called");
            val playlist = DevByteNetwork.events.getEventsList()
            database.eventDao.insertAll(playlist.asDatabaseModel())
        }
    }
}
