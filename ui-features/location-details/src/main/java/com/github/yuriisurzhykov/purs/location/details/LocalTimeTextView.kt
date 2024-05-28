package com.github.yuriisurzhykov.purs.location.details

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.yuriisurzhykov.purs.domain.model.TimeSlot
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun LocalTimeTextView(time: TimeSlot, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.format_patter_time).format(
            time.startTime.toFormattedString(),
            time.endTime.toFormattedString()
        ),
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge
    )
}

internal fun LocalTime.toFormattedString(): String {
    return formatTime(this)
}

private fun formatTime(time: LocalTime): String {
    // Format the time using the default locale and the following format: 1PM, 12AM
    val format = DateTimeFormatter.ofPattern("ha", Locale.getDefault())
    return time.format(format)
}