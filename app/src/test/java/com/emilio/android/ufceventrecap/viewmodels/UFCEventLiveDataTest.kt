package com.emilio.android.ufceventrecap.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.emilio.android.ufceventrecap.database.getDatabase
import com.emilio.android.ufceventrecap.overview.DevByteViewModel
import com.emilio.android.ufceventrecap.repository.EventsRepository
import getOrAwaitValue
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
import org.mockito.junit.MockitoJUnit


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class UFCEventLiveDataTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var rule = MockitoJUnit.rule()

    private val testDispatcher = TestCoroutineDispatcher()

    // Subject under test
    private lateinit var viewModel: DevByteViewModel

    @Mock
    private lateinit var repo: EventsRepository

    @Before
    fun setupViewModel() {
        // Sets the given [dispatcher] as an underlying dispatcher of [Dispatchers.Main].
        // All consecutive usages of [Dispatchers.Main] will use given [dispatcher] under the hood.
        Dispatchers.setMain(testDispatcher)

        viewModel = DevByteViewModel(ApplicationProvider.getApplicationContext())

        repo = EventsRepository(getDatabase(ApplicationProvider.getApplicationContext()))
    }

    @After
    fun tearDown() {
        // Resets state of the [Dispatchers.Main] to the original main dispatcher.
        // For example, in Android Main thread dispatcher will be set as [Dispatchers.Main].
        Dispatchers.resetMain()

        // Clean up the TestCoroutineDispatcher to make sure no other work is running.
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `call refreshUFCEvents and verify that eventNetworkError is set to false`() = runBlocking {
        suspend { repo.refreshUFCEvents() }

        junit.framework.Assert.assertNotNull(viewModel.eventNetworkError)
        viewModel.eventNetworkError.let { junit.framework.Assert.assertFalse(it.getOrAwaitValue()) }

        return@runBlocking
    }

    @Test
    fun `call refreshUFCEvents and verify that isNetworkErrorShown is set to false`() = runBlocking {
        suspend { repo.refreshUFCEvents() }

        junit.framework.Assert.assertNotNull(viewModel.isNetworkErrorShown)
        viewModel.isNetworkErrorShown.let { junit.framework.Assert.assertFalse(it.getOrAwaitValue()) }

        return@runBlocking
    }

}