package com.github.yuriisurzhykov.purs.domain.chain

import com.github.yuriisurzhykov.purs.domain.model.WorkingDay

interface WorkingHourChainProcessor : ProcessWorkingHoursCollection {

    class Base(
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