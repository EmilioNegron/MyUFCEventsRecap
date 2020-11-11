
package com.emilio.android.ufceventrecap.network

import com.emilio.android.ufceventrecap.database.DatabaseEvent
import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 *
 * @see domain package for
 */

/**
 * EventHolder holds a list of UFC Events.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "events": []
 * }
 */
@JsonClass(generateAdapter = true)
data class NetworkEventContainer(val events: List<NetworkEvent>)

/**
 * Events represent a devbyte that can be played.
 */
@JsonClass(generateAdapter = true)
data class NetworkEvent(
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
 * Convert Network results to database objects
 */
fun NetworkEventContainer.asDatabaseModel(): List<DatabaseEvent> {
    return events.map {
        DatabaseEvent(
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

