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

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EventDao {
    @Query("select * from DatabaseEvent")
    fun getEvents(): LiveData<List<DatabaseEvent>>

    @Query("DELETE FROM DatabaseEvent")
    fun deleteAllFromTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<DatabaseEvent>)
}



@Database(entities = [DatabaseEvent::class], version = 1, exportSchema = false)
abstract class EventsDatabase: RoomDatabase() {
    abstract val eventDao: EventDao
}

private lateinit var INSTANCE: EventsDatabase

fun getDatabase(context: Context): EventsDatabase {
    synchronized(EventsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context,
                    EventsDatabase::class.java,
                    "events").build()
        }
    }
    return INSTANCE
}
