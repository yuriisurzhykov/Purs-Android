package com.github.yuriisurzhykov.purs.domain.chain

import com.github.yuriisurzhykov.purs.domain.model.WorkingDay
import javax.inject.Inject

/**
 * The base interface for processing working hours collection. This is the primary
 * class that contains all other processing chains and iterate over the chain to process
 * the working hours collection.
 * */
interface WorkingHourChainProcessor : ProcessWorkingHoursCollection {

    class Base @Inject constructor(
        private vararg val processors: ProcessWorkingHoursCollection
    ) : WorkingHourChainProcessor {
        override fun process(collection: Collection<WorkingDay>): Collection<WorkingDay> {
            var processCollection = collection
            processors.forEach { processor ->
                processCollection = processor.process(processCollection)
            }
            return processCollection
        }
    }
}