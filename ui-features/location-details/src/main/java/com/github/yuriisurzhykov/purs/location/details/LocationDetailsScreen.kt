package com.github.yuriisurzhykov.purs.location.details

import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.github.yuriisurzhykov.purs.domain.model.Location
import com.github.yuriisurzhykov.purs.domain.model.LocationStatus
import com.github.yuriisurzhykov.purs.domain.model.WorkingDay
import com.github.yuriisurzhykov.purs.location.uikit.theme.DefaultCornerRadius
import com.github.yuriisurzhykov.purs.location.uikit.theme.DefaultPadding
import com.github.yuriisurzhykov.purs.location.uikit.theme.SmallPadding
import com.github.yuriisurzhykov.purs.location.uikit.theme.TinyPadding
import java.time.LocalDate
import java.util.Locale


@Composable
fun LocationDetails(modifier: Modifier) {
    LocationDetails(viewModel = viewModel(), modifier = modifier)
}

@Composable
internal fun LocationDetails(
    viewModel: LocationDetailsViewModel,
    modifier: Modifier = Modifier
) {
    val state: State by viewModel.detailsResponse.collectAsStateWithLifecycle(lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current)
    BackgroundImage(modifier = Modifier.fillMaxSize())
    if (state != State.None) {
        Content(state = state, modifier = modifier.fillMaxSize())
    }
}

@Composable
internal fun Content(state: State, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        if (state is State.Success && state.location != null) {
            LocationDetailsMain(state.location)
        }
        if (state is State.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(DefaultPadding)
            )
            if (state.location != null) {
                LocationDetailsMain(location = state.location)
            }
        }
        if (state is State.Error) {
            if (state.location != null) {
                LocationDetailsMain(location = state.location)
            }
            state.error?.let {
                Toast.makeText(LocalContext.current, it.message.orEmpty(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}

@Composable
internal fun LocationDetailsMain(location: Location) {
    Text(
        text = location.locationName,
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 54.sp,
            color = Color.White,
            textAlign = TextAlign.Start
        ),
        textAlign = TextAlign.Justify,
        modifier = Modifier.padding(DefaultPadding)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        OperatingHoursBox(location = location)
        Box(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.BottomCenter)
        ) {
            ArrowAnimation()
        }
    }
}

@Composable
internal fun OperatingHoursBox(location: Location, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val expandIconAngle = remember { Animatable(0f) }

    LaunchedEffect(key1 = expanded) {
        expandIconAngle.animateTo(
            targetValue = (expandIconAngle.value + 90) % 180,
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = 0,
                easing = LinearOutSlowInEasing
            )
        )
    }

    Column(
        modifier = modifier
            .padding(DefaultPadding)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    colorResource(id = R.color.background_card_semitransprent),
                    shape = if (expanded) {
                        RoundedCornerShape(
                            topStart = DefaultCornerRadius,
                            topEnd = DefaultCornerRadius
                        )
                    } else {
                        RoundedCornerShape(DefaultCornerRadius)
                    }
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        radius = DefaultCornerRadius,
                        color = colorResource(id = R.color.background_card_semitransprent)
                    ),
                    onClick = {
                        expanded = !expanded
                    })
                .padding(DefaultPadding),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        location.status?.let { status ->
                            LocationStatusText(
                                status,
                                modifier = Modifier.padding(end = DefaultPadding)
                            )
                        }
                        Spacer(modifier = Modifier.width(SmallPadding))
                        location.status?.let { status ->
                            LocationStatusBadge(status)
                        }
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.chevron_right),
                        contentDescription = null,
                        modifier = Modifier.rotate(expandIconAngle.value)
                    )
                }
                Text(
                    text = stringResource(id = R.string.label_see_full_hours),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .background(
                        colorResource(id = R.color.background_card_semitransprent),
                        shape = RoundedCornerShape(
                            bottomStart = DefaultCornerRadius,
                            bottomEnd = DefaultCornerRadius
                        )
                    )
                    .padding(DefaultPadding)
            ) {
                HorizontalDivider(color = Color.Gray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(TinyPadding))
                LazyColumn {
                    items(location.workingDays.size) {
                        val workingHour = location.workingDays.toList()[it]
                        WorkingHourView(workingHour)
                    }
                }
            }
        }
    }
}

@Composable
fun LocationStatusBadge(status: LocationStatus, modifier: Modifier = Modifier) {
    when (status) {
        is LocationStatus.Open -> ColorBadge(color = R.color.color_green, modifier)

        is LocationStatus.Closing,
        is LocationStatus.ClosingSoon,
        is LocationStatus.ClosingSoonLongReopen -> ColorBadge(
            color = R.color.color_yellow,
            modifier
        )

        is LocationStatus.ClosedOpenSoon -> ColorBadge(color = R.color.color_red, modifier)
        is LocationStatus.ClosedFully -> ColorBadge(color = R.color.color_red, modifier)
        is LocationStatus.Closed -> ColorBadge(color = R.color.color_red, modifier)
    }
}

@Composable
fun ColorBadge(@ColorRes color: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(8.dp)
            .background(
                color = colorResource(id = color),
                shape = MaterialTheme.shapes.small
            )
    )
}

@Composable
fun LocationStatusText(status: LocationStatus, modifier: Modifier = Modifier) {
    when (status) {
        is LocationStatus.Open -> LocationStatusTextView(
            text = stringResource(id = R.string.label_location_open).format(
                status.closeTime.toFormattedString()
            ),
            modifier = modifier
        )

        is LocationStatus.Closing -> LocationStatusTextView(
            text = stringResource(id = R.string.label_location_closing).format(
                status.closeTime.toFormattedString()
            ),
            modifier = modifier
        )

        is LocationStatus.ClosingSoon -> LocationStatusTextView(
            text = stringResource(id = R.string.label_location_closing_soon).format(
                status.closeTime.toFormattedString(),
                status.reopenTime.toFormattedString()
            ),
            modifier = modifier
        )

        is LocationStatus.ClosedOpenSoon -> LocationStatusTextView(
            text = stringResource(id = R.string.label_location_closed_opens_soon).format(
                status.reopenTime.toFormattedString()
            ),
            modifier = modifier
        )

        is LocationStatus.Closed -> LocationStatusTextView(
            text = stringResource(id = R.string.label_location_closed_opens_then).format(
                status.openDay,
                status.openTime.toFormattedString()
            ),
            modifier = modifier
        )

        is LocationStatus.ClosingSoonLongReopen -> LocationStatusTextView(
            text = stringResource(id = R.string.label_location_closing_soon_reopen_then).format(
                status.closeTime.toFormattedString(),
                status.reopenDay,
                status.reopenTime.toFormattedString()
            ),
            modifier = modifier
        )

        is LocationStatus.ClosedFully -> LocationStatusTextView(
            text = stringResource(id = R.string.label_location_closed),
            modifier = modifier
        )
    }
}

@Composable
fun LocationStatusTextView(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun BackgroundImage(modifier: Modifier = Modifier) {
    AsyncImage(
        model = R.drawable.screen_background,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

@Composable
fun WorkingHourView(workingDay: WorkingDay) {
    val textWeight = if (workingDay.weekDay == LocalDate.now().dayOfWeek) {
        FontWeight.Bold
    } else {
        FontWeight.Normal
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(TinyPadding),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = workingDay.weekDay.getDisplayName(
                java.time.format.TextStyle.FULL,
                Locale.getDefault()
            ), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = textWeight)
        )
        Column {
            if (workingDay.scheduleList.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.label_location_closed),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = textWeight)
                )
            } else if (workingDay.open24H()) {
                Text(
                    text = stringResource(id = R.string.label_location_open_24h),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = textWeight)
                )
            } else {
                workingDay.scheduleList.forEach { timeSlot ->
                    LocalTimeTextView(
                        timeSlot,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = textWeight)
                    )
                }
            }
        }
    }
}