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

package com.emilio.android.ufceventrecap.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emilio.android.ufceventrecap.domain.DevByteEvent


/**
 * Database entities go in this file. These are responsible for reading and writing from the
 * database.
 */


/**
 * DatabaseEvent represents a ufc event entity in the database.
 */
@Entity
data class DatabaseEvent(
        @PrimaryKey
        val idEvent: String,
        val strFilename: String,
        val strDescriptionEN: String?,
        val strEvent: String,
        val strSeason: String,
        val dateEvent: String,
        val strTime: String,
        val strCity: String,
        val strVenue: String,
        val strThumb: String?)


/**
 * Map DatabaseEvents to domain entities
 */
fun List<DatabaseEvent>.asDomainModel(): List<DevByteEvent> {
    return map {
        DevByteEvent(
                idEvent = it.idEvent,
                strFilename = it.strFilename,
                strDescriptionEN = it.strDescriptionEN,
                strEvent = it.strEvent,
                strSeason = it.strSeason,
                dateEvent = it.dateEvent,
                strTime = it.strTime,
                strCity = it.strCity,
                strVenue = it.strVenue,
                strThumb = it.strThumb)
    }
}
