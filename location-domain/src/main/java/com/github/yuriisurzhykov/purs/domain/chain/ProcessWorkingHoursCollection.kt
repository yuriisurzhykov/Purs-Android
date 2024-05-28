package com.github.yuriisurzhykov.purs.domain.chain

import com.github.yuriisurzhykov.purs.domain.model.WorkingHour

interface ProcessWorkingHoursCollection {

    fun process(collection: Collection<WorkingHour>): Collection<WorkingHour>
}