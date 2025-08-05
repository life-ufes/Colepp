package com.example.transferdata.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.transferdata.R
import com.example.transferdata.common.WearableState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState

@Composable
fun WearApp(mainViewModel: MainViewModel) {
    AppScaffold {
        val navController = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(navController = navController, startDestination = "main") {
            composable("main") {
                MainScreen(
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel
) {
    val status = mainViewModel.wearableState.collectAsState()
    val hr = mainViewModel.hrData.collectAsState()
    MainScreen(
        status = status.value,
        hr = hr.value
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun MainScreen(
    status: WearableState,
    hr: Int?
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Chip,
            last = ItemType.Text
        )
    )

    ScreenScaffold(
        modifier = Modifier.fillMaxSize(),
        scrollState = columnState
    ) {
        when (status) {
            WearableState.Waiting -> {
                WaitingScreen()
            }

            WearableState.Transferring -> {
                TransferringScreen(
                    hr = hr
                )
            }
        }
    }
}

@Composable
private fun WaitingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_hourglass),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp),
            tint = colorResource(id = R.color.hourglass)
        )
        Text(
            text = stringResource(R.string.waiting_for_data),
            color = Color.Gray,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TransferringScreen(
    hr: Int?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.transferring_data),
            color = Color.White
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            Text(
                text = "${hr ?: "-"}",
                color = Color.White,
                modifier = Modifier
                    .padding(8.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_heart),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MainScreenPreviewEvents() {
    MainScreen(
        status = WearableState.Transferring,
        hr = 120
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MainScreenPreviewEmpty() {
    MainScreen(
        status = WearableState.Waiting,
        hr = null
    )
}