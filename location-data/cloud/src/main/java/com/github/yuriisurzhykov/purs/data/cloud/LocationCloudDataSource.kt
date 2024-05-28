package com.github.yuriisurzhykov.purs.data.cloud

import com.github.yuriisurzhykov.purs.core.RequestResult
import com.github.yuriisurzhykov.purs.data.cloud.model.LocationCloud
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException


interface LocationCloudDataSource {

    suspend fun fetchLocation(): Flow<RequestResult<LocationCloud>>

    class Base(
        private val locationService: LocationService
    ) : LocationCloudDataSource {
        override suspend fun fetchLocation(): Flow<RequestResult<LocationCloud>> {
            return flow {
                // Start progress
                emit(RequestResult.InProgress())
                try {
                    // Fetch location details from the cloud
                    val locationResponse = locationService.fetchLocation()
                    if (locationResponse.isSuccessful) {
                        // Map body to whether success response or error if body is empty or null
                        val responseBody = locationResponse.body()
                        if (responseBody != null) {
                            emit(RequestResult.Success(responseBody))
                        } else {
                            emit(RequestResult.Error(data = null, error = null))
                        }
                    } else {
                        // if response is not successful then map error to error response
                        val error = ServerError(
                            locationResponse.code(),
                            locationResponse.errorBody()?.string().orEmpty()
                        )
                        val errorResponse = RequestResult.Error(null, error)
                        emit(errorResponse)
                    }
                } catch (e: IOException) {
                    val errorResponse = RequestResult.Error(data = null, error = e)
                    emit(errorResponse)
                }
            }
        }
    }
}