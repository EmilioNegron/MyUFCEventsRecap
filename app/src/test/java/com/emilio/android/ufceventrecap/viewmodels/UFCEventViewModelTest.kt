package com.emilio.android.ufceventrecap.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.emilio.android.ufceventrecap.UFCEventsApplication
import com.emilio.android.ufceventrecap.database.EventsDatabase
import com.emilio.android.ufceventrecap.domain.DevByteEvent
import com.emilio.android.ufceventrecap.overview.DevByteViewModel
import com.emilio.android.ufceventrecap.repository.EventsRepository
import junit.framework.Assert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.io.IOException


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class UFCEventViewModelTest {
    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var eventNetworkError: LiveData<Boolean>

   private val events = listOf(
      DevByteEvent("1059016",
            "UFC 2020-10-24 UFC 254 Khabib vs. Gaethje",
            "UFC 254: Khabib vs. Gaethje was a mixed martial arts event produced by the Ultimate Fighting Championship that took place on October 24, 2020 at the Flash Forum on Yas Island, Abu Dhabi, United Arab Emirates.\r\n\r\nBackground:\r\nAfter his frustrated fight against Tony Ferguson over the lockdowns caused by the pandemic, Khabib Nurmagomedov puts his UFC lightweight champion belt back on the line, and does so to unify the title with Justin Gaethje, interim champion of the category. after beating precisely Tony Ferguson. Khabib has not fought in a year, when he successfully defended his champion status against Dustin Poirier.\r\n\r\nMain Card:\r\nLightweight / Khabib Nurmagomedov (C) vs. Justin Gaethje (IC)\r\nMiddleweight / Robert Whittaker vs. Jared Cannonier\r\nHeavyweight / Alexander Volkov vs. Walt Harris\r\nMiddleweight / Phil Hawes vs. Jacob Malkoun\r\nWomen’s Flyweight / Lauren Murphy vs. Liliya Shakirova\r\nLight Heavyweight / Magomed Ankalaev vs. Ion Cuțelaba\r\n\r\nPreliminary Card:\r\nHeavyweight / Tai Tuivasa vs. Stefan Struve\r\nCatchweight / Casey Kenney vs. Nathaniel Wood\r\nCatchweight / Shavkat Rakhmonov vs. Alex Oliveira\r\nLight Heavyweight / Da Un Jung vs. Sam Alvey\r\n\r\nEarly Preliminary Card:\r\nWomen's Flyweight / Miranda Maverick vs. Liana Jojua\r\nCatchweight / Joel Álvarez vs. Alexander Yakovlev",
            "UFC 254 Khabib vs Gaethje",
            "2020",
            "2020-10-24",
            "18:00:00",
            "Abu Dhabi",
            "Flash Forum",
            "https://www.thesportsdb.com/images/media/event/thumb/146wcr1603391327.jpg"),
      DevByteEvent("1058302",
              "UFC 2020-10-18 UFC Fight Night 180 Ortega vs. The Korean Zombie",
              "Sixth appointment with Fight Island, in the octagon that the UFC has organized in the Arab Emirates. Jessica Andrade returns to flyweight to face Katlyn Cookagian: # 2 against # 1 in the ranking, with a pass up for grabs to challenge champion Valentina Shevchenko. In the main event, however, a featherweight challenge between Brian Ortega, T-City, and Chan Sung Jung, better known as The Korean Zombie.",
              "UFC Fight Night 180 Ortega vs The Korean Zombie",
              "2020",
              "2020-10-18",
              "00:00:00",
              "Yas Island, Abu Dhabi",
              "Flash Forum",
              "https://www.thesportsdb.com/images/media/event/thumb/af67801602949803.jpg")
    )

    @Spy
    private val eventListLiveData: MutableLiveData<List<DevByteEvent>> = MutableLiveData()

    @Mock
    private lateinit var viewModel: DevByteViewModel

    @Mock
    private lateinit var repo: EventsRepository

    @Mock
    private lateinit var application: UFCEventsApplication

    @Mock
    private lateinit var database: EventsDatabase

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        // Sets the given [dispatcher] as an underlying dispatcher of [Dispatchers.Main].
        // All consecutive usages of [Dispatchers.Main] will use given [dispatcher] under the hood.
        Dispatchers.setMain(testDispatcher)

        //`when`(repo.events).thenReturn(eventListLiveData)
        viewModel = DevByteViewModel(application)

        eventNetworkError = viewModel.eventNetworkError

    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        // Resets state of the [Dispatchers.Main] to the original main dispatcher.
        // For example, in Android Main thread dispatcher will be set as [Dispatchers.Main].
        Dispatchers.resetMain()

        // Clean up the TestCoroutineDispatcher to make sure no other work is running.
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `isNetworkError is true when repo throws exception`() = runBlocking {
        eventNetworkError = viewModel.eventNetworkError
        var isNetworkError = eventNetworkError.value
        assertNotNull(isNetworkError)
        isNetworkError?.let { assertFalse(it) }

        // Mock IOException
        suspend { `when`(viewModel.refreshDataFromRepository()).thenAnswer { throw IOException() } }

       isNetworkError = viewModel.isPlaylistEmpty()
        assertNotNull(isNetworkError)
        isNetworkError?.let { assertTrue(it) }

        suspend {
            verify(viewModel).refreshDataFromRepository()

            isNetworkError = viewModel.isPlaylistEmpty()
            assertNotNull(isNetworkError)
            isNetworkError?.let { assertFalse(it) }
        }

        return@runBlocking
    }

    @Test
    fun `retrieves local ufc events without calling refreshUFCEvents`() = runBlocking {
        eventListLiveData.value = events

        verify(repo, never()).refreshUFCEvents()

        suspend {
            viewModel.myEvents

            val isError = viewModel.isPlaylistEmpty()
            assertNotNull(isError)
            isError?.let { assertFalse(it) }
        }

        return@runBlocking
    }

    @Test
    fun `call refreshUFCEvents to get events if not locally present`() = runBlocking {
        // Refresh List as usual
        var isEmpty = false
        suspend {
            verify(repo.refreshUFCEvents())

            // verify Local List exists
            isEmpty = viewModel.isPlaylistEmpty()!!
            assertNotNull(isEmpty)
            isEmpty.let { assertFalse(it) }
        }

        // Remove all Local events from DB
        suspend {
            database.eventDao.deleteAllFromTable()

            isEmpty = viewModel.isPlaylistEmpty()!!
            assertNotNull(isEmpty)
            isEmpty.let { assertTrue(it) }
        }

        // Refresh again
        suspend {
            verify((repo).refreshUFCEvents())

            isEmpty = viewModel.isPlaylistEmpty()!!
            assertNotNull(isEmpty)
            isEmpty.let { assertFalse(it) }
        }

        return@runBlocking
    }
}