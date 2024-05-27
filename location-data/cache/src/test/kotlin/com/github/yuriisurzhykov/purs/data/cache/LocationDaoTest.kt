package com.github.yuriisurzhykov.purs.data.cache

import com.github.yuriisurzhykov.purs.data.cache.model.LocationCache
import com.github.yuriisurzhykov.purs.data.cache.model.LocationWithWorkingHours
import com.github.yuriisurzhykov.purs.data.cache.model.WorkingHourCache
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LocationDaoTest {

    @Test
    fun `check persist location structure and working hours with correct id`() = runTest {
        val locationDao = TestLocationDao()

        val fakeLocation = LocationCache(1, "Test location")
        val fakeWorkingHour = WorkingHourCache(
            1,
            "12:00:00",
            "15:00:00",
            "Mon",
            1
        )
        val fakeLocationEmbedded = LocationWithWorkingHours(fakeLocation, listOf(fakeWorkingHour))
        locationDao.insert(fakeLocationEmbedded)
        assertEquals(locationDao.persistedLocation, fakeLocation)
        assertEquals(locationDao.persistedWorkingHour, fakeWorkingHour)
    }

    private class TestLocationDao : LocationDao() {
        var persistedLocation: LocationCache? = null
        var persistedWorkingHour: WorkingHourCache? = null
        override suspend fun getLocation(id: Long): LocationWithWorkingHours? = null

        override suspend fun insert(location: LocationCache): Long {
            persistedLocation = location
            return location.locationId
        }

        override suspend fun insert(workingHour: WorkingHourCache): Long {
            persistedWorkingHour = workingHour
            return workingHour.workingHourId
        }
    }
}