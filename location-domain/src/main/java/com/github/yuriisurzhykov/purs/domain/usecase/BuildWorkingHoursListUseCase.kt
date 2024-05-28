package com.github.yuriisurzhykov.purs.domain.usecase

import com.github.yuriisurzhykov.purs.core.RequestResult
import com.github.yuriisurzhykov.purs.core.map
import com.github.yuriisurzhykov.purs.data.repository.LocationRepository
import com.github.yuriisurzhykov.purs.domain.chain.MergeTimeSlotsUseCase
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
        private val mergeTimeSlotsUseCase: MergeTimeSlotsUseCase,
        private val processor: WorkingHourChainProcessor
    ) : BuildWorkingHoursListUseCase {

        override fun workingHours(): Flow<RequestResult<Location>> {
            return flow {
                val repositoryResult = repository.fetchLocationDetails()
                    .map { requestResult ->
                        requestResult.map { cache ->
                            val mapped = mapper.map(cache)
                            mapped.copy(
                                workingHours = mapped.workingHours.map {
                                    mergeTimeSlotsUseCase.mergeTimeSlots(it)
                                }.toSet()
                            )
                        }.map {
                            it.copy(workingHours = processor.process(it.workingHours))
                        }
                    }
                emitAll(repositoryResult)
            }
        }
    }
}