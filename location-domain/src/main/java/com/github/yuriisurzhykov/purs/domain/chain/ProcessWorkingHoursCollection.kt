package com.github.yuriisurzhykov.purs.domain.chain

import com.github.yuriisurzhykov.purs.domain.model.WorkingDay

/**
 * Chain processor for the list of working hours.
 * */
interface ProcessWorkingHoursCollection {

    fun process(collection: Collection<WorkingDay>): Collection<WorkingDay>
}