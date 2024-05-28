package com.github.yuriisurzhykov.purs.location.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.yuriisurzhykov.purs.core.RequestResult
import com.github.yuriisurzhykov.purs.domain.model.Location
import com.github.yuriisurzhykov.purs.domain.usecase.BuildWorkingHoursListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class LocationDetailsViewModel @Inject constructor(
    fetchLocationDetailsUseCase: Provider<BuildWorkingHoursListUseCase>
) : ViewModel() {

    val detailsResponse: StateFlow<RequestResult<Location>> =
        fetchLocationDetailsUseCase.get().workingHours()
            .stateIn(viewModelScope, SharingStarted.Lazily, RequestResult.InProgress())
}