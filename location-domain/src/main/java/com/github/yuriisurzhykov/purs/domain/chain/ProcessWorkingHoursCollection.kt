package com.github.yuriisurzhykov.purs.domain.chain

import com.github.yuriisurzhykov.purs.domain.model.WorkingDay

interface ProcessWorkingHoursCollection {

    fun process(collection: Collection<WorkingDay>): Collection<WorkingDay>
}