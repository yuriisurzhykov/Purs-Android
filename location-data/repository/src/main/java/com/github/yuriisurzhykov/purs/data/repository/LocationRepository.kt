package com.github.yuriisurzhykov.purs.data.repository

import com.github.yuriisurzhykov.purs.core.MergeStrategy
import com.github.yuriisurzhykov.purs.core.RequestResult
import com.github.yuriisurzhykov.purs.core.map
import com.github.yuriisurzhykov.purs.data.cache.LocationCacheDataSource
import com.github.yuriisurzhykov.purs.data.cache.model.LocationWithWorkingHours
import com.github.yuriisurzhykov.purs.data.cloud.LocationCloudDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

interface LocationRepository {

    suspend fun fetchLocationDetails(): Flow<RequestResult<LocationWithWorkingHours>>

    class Base @Inject constructor(
        private val cloudDataSource: LocationCloudDataSource,
        private val cacheDataSource: LocationCacheDataSource,
        private val sourceMergeStrategy: MergeStrategy<RequestResult<LocationWithWorkingHours>>,
        private val mapper: LocationCloudToCacheMapper
    ) : LocationRepository {
        override suspend fun fetchLocationDetails(): Flow<RequestResult<LocationWithWorkingHours>> {
            // Fetch cached data if any
            val cachedResponse = cacheDataSource.fetchLocation()
            // Start loading data from cloud
            val cloudResponse = cloudDataSource.fetchLocation()
                .map { response ->      // Map cloud data to cache data
                    response.map { it.let { locationCloud -> mapper.map(locationCloud) } }
                }.onEach { response ->  // Persist cloud data to cache
                    response.map { cacheDataSource.persistLocation(it) }
                }

            // Produce initial state that is loading
            val initial =
                flowOf<RequestResult<LocationWithWorkingHours>>(RequestResult.InProgress())

            // Combine two data sources together with the strategy based on cloud and cache results
            val combinedSources = cachedResponse.combine(cloudResponse) { cache, cloud ->
                sourceMergeStrategy.merge(cache, cloud)
            }
            return merge(initial, combinedSources)
        }
    }
}