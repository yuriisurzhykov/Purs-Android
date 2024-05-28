package com.github.yuriisurzhykov.purs.location.details

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.yuriisurzhykov.purs.core.RequestResult
import com.github.yuriisurzhykov.purs.domain.model.Location


@Composable
fun LocationDetails() {
    LocationDetails(viewModel = viewModel())
}

@Composable
internal fun LocationDetails(viewModel: LocationDetailsViewModel) {
    when (val state = viewModel.detailsResponse.collectAsState().value) {
        is RequestResult.InProgress -> InProgressView()
        is RequestResult.Success -> SuccessView(state.data)
        is RequestResult.Error -> ErrorView()
    }
}

@Composable
fun ErrorView() {
    Text("Error", color = Color.Red)
}

@Composable
fun SuccessView(data: Location) {
    LazyColumn {
        items(data.workingHours.size) {
            Row {
                Text(text = data.workingHours.toList()[it].dayName)
                Text(data.workingHours.toList()[it].scheduleList.joinToString())
            }
        }
    }
}

@Composable
fun InProgressView(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier)
}