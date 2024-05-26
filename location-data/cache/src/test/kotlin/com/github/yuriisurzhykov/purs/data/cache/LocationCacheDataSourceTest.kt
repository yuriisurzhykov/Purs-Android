package com.github.yuriisurzhykov.purs.data.cache

import com.github.yuriisurzhykov.purs.core.RequestResult
import com.github.yuriisurzhykov.purs.data.cache.model.LocationCache
import com.github.yuriisurzhykov.purs.data.cache.model.LocationWithWorkingHours
import com.github.yuriisurzhykov.purs.data.cache.model.WorkingHourCache
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LocationCacheDataSourceTest {

    @Test
    fun `check data source emits success location with hours entity if exists`() = runTest {
        val getLocationId = GetLocationId.Const(1)
        val locationDao = mockk<LocationDao>(relaxed = true)
        val expectedLocation = LocationWithWorkingHours(
            LocationCache(1, "Test location"),
            listOf(
                WorkingHourCache(
                    1,
                    "12:00:00",
                    "15:00:00",
                    1
                )
            )
        )
        coEvery { locationDao.getLocation(any()) } answers {
            expectedLocation
        }
        val dataSource = LocationCacheDataSource.Base(locationDao, getLocationId)


        val actual = dataSource.fetchLocation().first()
        val expected = RequestResult.Success(expectedLocation)

        assertEquals(expected, actual)
    }

    @Test
    fun `check data source emits error if no location found`() = runTest {
        val getLocationId = GetLocationId.Const(1)
        val locationDao = mockk<LocationDao>(relaxed = true)
        val expectedLocation: LocationWithWorkingHours? = null
        coEvery { locationDao.getLocation(any()) } answers {
            expectedLocation
        }
        val dataSource = LocationCacheDataSource.Base(locationDao, getLocationId)


        val actual = dataSource.fetchLocation().first()
        val expected = RequestResult.Error(
            expectedLocation,
            NoValueFoundException("Location with ID 1 not found")
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `check data source emits error response of exception occur`() = runTest {
        val getLocationId = GetLocationId.Const(1)
        val locationDao = mockk<LocationDao>(relaxed = true)
        val expectedLocation: LocationWithWorkingHours? = null
        val mockException = RuntimeException()
        coEvery { locationDao.getLocation(any()) } throws mockException
        val dataSource = LocationCacheDataSource.Base(locationDao, getLocationId)


        val actual = dataSource.fetchLocation().first()
        val expected = RequestResult.Error(expectedLocation, mockException)

        assertEquals(expected, actual)
    }

    @Test
    fun `check data source triggers persist location dao method`() = runTest {
        val getLocationId = GetLocationId.Const(1)
        val locationDao = mockk<LocationDao>(relaxed = true)
        val dataSource = LocationCacheDataSource.Base(locationDao, getLocationId)


        val fakeLocation = LocationWithWorkingHours(
            LocationCache(1, "Test location"),
            listOf(
                WorkingHourCache(
                    1,
                    "12:00:00",
                    "15:00:00",
                    1
                )
            )
        )
        dataSource.persistLocation(fakeLocation)

        coVerify(exactly = 1) {
            locationDao.insert(fakeLocation)
        }
    }
}