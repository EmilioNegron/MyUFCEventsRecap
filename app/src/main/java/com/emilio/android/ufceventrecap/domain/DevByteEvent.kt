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

package com.emilio.android.ufceventrecap.domain

import android.os.Parcelable
import com.emilio.android.ufceventrecap.util.smartTruncate
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.util.*

/**
 * Domain objects are plain Kotlin data classes that represent the things in our app. These are the
 * objects that should be displayed on screen, or manipulated by the app.
 *
 * @see database for objects that are mapped to the database
 * @see network for objects that parse or prepare network calls
 */

/**
 * Events represents a UFC Event that can be displayed.
 */
@Parcelize
data class DevByteEvent(val idEvent: String,
                        val strFilename: String,
                        val strDescriptionEN: String?,
                        var strEvent: String,
                        val strSeason: String,
                        val dateEvent: String,
                        val strTime: String,
                        val strCity: String,
                        val strVenue: String,
                        val strThumb: String?) : Parcelable {

    /**
     * Short description is used for displaying truncated descriptions in the UI
     */
    val shortDescription: String?
        get() = strDescriptionEN?.smartTruncate(200)

    /**
     * Date Format is used for displaying readable Date in the UI
     */
    // TODO: This causes an exception ==>
    //  "Caused by: java.text.ParseException: Unparseable date: "2020-09-13""
    // Will convert to use Moshi Date Format at a later time. :)
    /*val date: String?
        get() = DateFormat.getDateInstance(DateFormat.LONG, Locale.US)?.parse(dateEvent).toString()*/
}