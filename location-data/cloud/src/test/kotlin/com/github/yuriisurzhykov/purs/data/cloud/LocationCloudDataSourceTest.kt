package com.github.yuriisurzhykov.purs.data.cloud

import com.github.yuriisurzhykov.purs.core.RequestResult
import com.github.yuriisurzhykov.purs.data.cloud.model.LocationCloud
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.net.ConnectException

class LocationCloudDataSourceTest {

    @Test
    fun `check data source emits success with location`() = runTest {
        val locationService = mockk<LocationService>(relaxed = true)
        val mockLocationCloud = mockk<LocationCloud.Base>()
        coEvery { locationService.fetchLocation() } returns Response.success(mockLocationCloud)
        val dataSource = LocationCloudDataSource.Base(locationService)

        val actual = dataSource.fetchLocation().toList()
        val expected = listOf(RequestResult.InProgress(), RequestResult.Success(mockLocationCloud))

        assertEquals(expected, actual)
    }

    @Test
    fun `check data source emits success with empty body`() = runTest {
        val locationService = mockk<LocationService>(relaxed = true)
        coEvery { locationService.fetchLocation() } returns Response.success(null)
        val dataSource = LocationCloudDataSource.Base(locationService)

        val actual = dataSource.fetchLocation().toList()
        val expected = listOf(RequestResult.InProgress(), RequestResult.Error(null, null))

        assertEquals(expected, actual)
    }

    @Test
    fun `check data source emits server error`() = runTest {
        val locationService = mockk<LocationService>(relaxed = true)
        coEvery { locationService.fetchLocation() } returns Response.error(
            404,
            "Page not found".toResponseBody("application/json".toMediaType())
        )
        val dataSource = LocationCloudDataSource.Base(locationService)

        val actual = dataSource.fetchLocation().toList()
        val expected = listOf(
            RequestResult.InProgress(),
            RequestResult.Error(null, ServerError(404, "Page not found"))
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `check data source emits error when exception occur`() = runTest {
        val locationService = mockk<LocationService>(relaxed = true)
        val exceptionToThrow = ConnectException("No internet")
        coEvery { locationService.fetchLocation() } throws exceptionToThrow
        val dataSource = LocationCloudDataSource.Base(locationService)

        val actual = dataSource.fetchLocation().toList()
        val expected =
            listOf(RequestResult.InProgress(), RequestResult.Error(null, exceptionToThrow))

        assertEquals(expected, actual)
    }
}