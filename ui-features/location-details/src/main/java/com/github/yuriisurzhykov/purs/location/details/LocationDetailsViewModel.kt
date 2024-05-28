package com.github.yuriisurzhykov.purs.location.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.yuriisurzhykov.purs.core.RequestResult
import com.github.yuriisurzhykov.purs.domain.model.Location
import com.github.yuriisurzhykov.purs.domain.usecase.BuildWorkingHoursListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
internal class LocationDetailsViewModel @Inject constructor(
    fetchLocationDetailsUseCase: Provider<BuildWorkingHoursListUseCase>
) : ViewModel() {

    val detailsResponse: StateFlow<State> =
        fetchLocationDetailsUseCase.get().workingHours()
            .map { it.toState() }
            .stateIn(viewModelScope, SharingStarted.Lazily, State.None)
}

private fun RequestResult<Location>.toState(): State {
    return when (this) {
        is RequestResult.Error -> State.Error(data, error)
        is RequestResult.InProgress -> State.Loading(data)
        is RequestResult.Success -> State.Success(data)
    }
}

internal sealed class State(val location: Location?) {
    data object None : State(location = null)

    class Loading(articles: Location? = null) : State(articles)

    class Error(location: Location? = null, val error: Throwable? = null) : State(location)

    class Success(location: Location) : State(location)
}