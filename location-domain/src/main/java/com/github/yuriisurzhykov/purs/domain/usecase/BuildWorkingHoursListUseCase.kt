package com.github.yuriisurzhykov.purs.domain.usecase

import com.github.yuriisurzhykov.purs.core.RequestResult
import com.github.yuriisurzhykov.purs.core.map
import com.github.yuriisurzhykov.purs.data.repository.LocationRepository
import com.github.yuriisurzhykov.purs.domain.chain.WorkingHourChainProcessor
import com.github.yuriisurzhykov.purs.domain.mapper.LocationCacheToDomainMapper
import com.github.yuriisurzhykov.purs.domain.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface BuildWorkingHoursListUseCase {

    fun workingHours(): Flow<RequestResult<Location>>

    class Base @Inject constructor(
        private val repository: LocationRepository,
        private val mapper: LocationCacheToDomainMapper,
        private val processor: WorkingHourChainProcessor,
        private val buildLocationStatus: BuildCurrentLocationStatusUseCase
    ) : BuildWorkingHoursListUseCase {

        override fun workingHours(): Flow<RequestResult<Location>> {
            return flow {
                val repositoryResult = repository.fetchLocationDetails()
                    .map { requestResult ->
                        requestResult
                            .map { cache -> mapper.map(cache) }
                            .map { location ->
                                location.copy(workingDays = processor.process(location.workingDays))
                            }
                            .map { location ->
                                location.copy(status = buildLocationStatus.currentStatus(location.workingDays))
                            }
                    }
                emitAll(repositoryResult)
            }
        }
    }
}