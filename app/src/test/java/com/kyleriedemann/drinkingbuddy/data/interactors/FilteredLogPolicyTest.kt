package com.kyleriedemann.drinkingbuddy.data.interactors

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.kyleriedemann.drinkingbuddy.data.models.Log
import com.kyleriedemann.drinkingbuddy.data.models.LogLevel
import com.kyleriedemann.drinkingbuddy.data.models.LogTag
import com.kyleriedemann.drinkingbuddy.data.source.local.LogDao
import com.kyleriedemann.drinkingbuddy.ui.log.FilterRepository
import com.kyleriedemann.drinkingbuddy.ui.log.LevelFilter
import com.kyleriedemann.drinkingbuddy.ui.log.LogFilters
import com.kyleriedemann.drinkingbuddy.ui.log.TagFilter
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.lang.ArithmeticException

class FilteredLogPolicyTest {

    @MockK lateinit var logDao: LogDao
    @MockK lateinit var filterRepository: FilterRepository
    val dispatcher: CoroutineDispatcher = TestCoroutineDispatcher()

    lateinit var spy: FilteredLogPolicy

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        spy = spyk(FilteredLogPolicy(logDao, filterRepository, dispatcher))
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `return all logs for empty filter`(): Unit = runBlockingTest {
        coEvery { filterRepository.filter } returns flowOf(LogFilters(LevelFilter(), TagFilter()))
        coEvery { logDao.getLogs() } returns flowOf(listOf(Log(LogLevel.Verbose, "Tag", "message")))

        spy.invoke().collect { assertThat(it).isNotEmpty() }

        coVerifyOrder {
            spy.invoke()
            filterRepository.filter
            logDao.getLogs()
        }

        confirmVerified(filterRepository, logDao, spy)
    }

    @Test
    fun `return logs filtered by tag for tag filter and empty level filter`(): Unit = runBlockingTest {
        val tag = LogTag("Device")
        coEvery { filterRepository.filter } returns flowOf(LogFilters(LevelFilter(), TagFilter(listOf(tag))))
        coEvery { logDao.getLogsFilteredByTag(arrayOf("Device")) } returns flowOf(listOf(Log(LogLevel.Verbose, "Device", "message")))

        spy.invoke().collect { assertThat(it).isNotEmpty() }

        coVerifyOrder {
            spy.invoke()
            filterRepository.filter
            logDao.getLogsFilteredByTag(arrayOf("Device"))
        }

        confirmVerified(filterRepository, logDao, spy)
    }

    @Test
    fun `return logs filtered by level for level filter and empty tag filter`(): Unit = runBlockingTest {
        coEvery { filterRepository.filter } returns flowOf(LogFilters(LevelFilter(info = true), TagFilter()))
        coEvery { logDao.getLogsFilteredByLevel(intArrayOf(LogLevel.Info.level)) } returns flowOf(listOf(Log(LogLevel.Info, "Device", "message")))

        spy.invoke().collect { assertThat(it).isNotEmpty() }

        coVerifyOrder {
            spy.invoke()
            filterRepository.filter
            logDao.getLogsFilteredByLevel(intArrayOf(LogLevel.Info.level))
        }

        confirmVerified(filterRepository, logDao, spy)
    }

    @Test
    fun `return logs filtered by level and tag for level and tag filters`(): Unit = runBlockingTest {
        val tag = LogTag("Device")
        coEvery { filterRepository.filter } returns flowOf(LogFilters(LevelFilter(info = true), TagFilter(listOf(tag))))
        coEvery { logDao.getLogsFilteredByLevelAndTag(intArrayOf(LogLevel.Info.level), arrayOf("Device")) } returns flowOf(listOf(Log(LogLevel.Info, "Device", "message")))

        spy.invoke().collect { assertThat(it).isNotEmpty() }

        coVerifyOrder {
            spy.invoke()
            filterRepository.filter
            logDao.getLogsFilteredByLevelAndTag(intArrayOf(LogLevel.Info.level), arrayOf("Device"))
        }

        confirmVerified(filterRepository, logDao, spy)
    }

    @Test
    fun `returns all logs if filter errors`(): Unit = runBlockingTest {
        coEvery { filterRepository.filter } returns flow { error("Oops") }
        coEvery { logDao.getLogs() } returns flowOf(listOf(Log(LogLevel.Verbose, "Tag", "message")))

        spy.invoke().collect { assertThat(it).isNotEmpty() }

        coVerifyOrder {
            spy.invoke()
            filterRepository.filter
            logDao.getLogs()
        }

        confirmVerified(filterRepository, logDao, spy)
    }

    @Test
    fun `returns empty list if database lookup fails`(): Unit = runBlockingTest {
        coEvery { filterRepository.filter } returns flow { error("Oops") }
        coEvery { logDao.getLogs() } returns flow { error("Oops") }

        spy.invoke().collect { assertThat(it).isEmpty() }

        coVerifyOrder {
            spy.invoke()
            filterRepository.filter
            logDao.getLogs()
        }

        confirmVerified(filterRepository, logDao, spy)
    }
}