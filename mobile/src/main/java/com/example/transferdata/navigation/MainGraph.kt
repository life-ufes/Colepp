package com.example.transferdata.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.transferdata.common.utils.JsonHandler
import com.example.transferdata.navigation.MainNavGraphArgs.RECORD_ID
import com.example.transferdata.navigation.MainNavGraphRoutes.HOME_GRAPH
import com.example.transferdata.navigation.MainNavGraphRoutes.HOME_SCREEN
import com.example.transferdata.navigation.MainNavGraphRoutes.RECORD_DETAIL_SCREEN
import com.example.transferdata.navigation.NewRecordingNavGraphRoutes.CREATE_NEW_RECORDING_GRAPH
import com.example.transferdata.presentation.home.HomeScreen
import com.example.transferdata.presentation.recordDetail.RecordDetailScreen

object MainNavGraphRoutes {
    const val HOME_GRAPH = "home_graph"
    const val HOME_SCREEN = "home_screen"
    const val RECORD_DETAIL_SCREEN = "record_detail_screen"
}

object MainNavGraphArgs {
    const val RECORD_ID = "record_id"
}

internal fun NavGraphBuilder.addMainGraph(
    navController: NavHostController,
    createDatasetFile: (Long) -> Unit,
    onBackPressed: () -> Unit,
    onClosePressed: () -> Unit
) {
    navigation(
        route = HOME_GRAPH,
        startDestination = HOME_SCREEN
    ) {
        composable(HOME_SCREEN) {
            HomeScreen(
                onRecordingPressed = {},
                createNewRecording = {
                    navController.navigate(
                        CREATE_NEW_RECORDING_GRAPH
                    )
                },
                onRecordClicked = { recordId ->
                    navController.navigate(
                        "$RECORD_DETAIL_SCREEN/$RECORD_ID=${
                            JsonHandler.getEncodedJsonParamAsUri(
                                recordId
                            )
                        }"
                    )
                },
            )
        }
        composable("$RECORD_DETAIL_SCREEN/${RECORD_ID}={${RECORD_ID}}") {
            RecordDetailScreen(
                onBackPressed = onBackPressed,
                createDatasetFile = createDatasetFile,
            )
        }
    }
}