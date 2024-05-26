package com.github.yuriisurzhykov.purs.data.cloud

import com.github.yuriisurzhykov.purs.data.cloud.model.LocationCloud
import com.github.yuriisurzhykov.purs.data.cloud.model.WorkingHourCloud
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LocationCloudMapperTest {

    @Test
    fun `check base provides correct properties to mapper`() = runTest {
        val locationName = "Location test"
        val workingHours = listOf(
            WorkingHourCloud.Base("Mon", "08:00:00", "12:00:00")
        )
        val location = LocationCloud.Base(locationName, workingHours)
        val mapper = mockk<LocationCloud.Mapper<Unit>>(relaxed = true)
        location.map(mapper)

        verify(exactly = 1) { mapper.map(locationName, workingHours) }
    }
}