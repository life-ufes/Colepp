package com.example.transferdata.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.transferdata.R
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
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
    MainScreen(
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun MainScreen(
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Chip,
            last = ItemType.Text
        )
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState,
            modifier = Modifier
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.hello_world, "world"),
                    color = Color.White
                )
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MainScreenPreviewEvents() {
    MainScreen(
    )
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MainScreenPreviewEmpty() {
    MainScreen(
    )
}