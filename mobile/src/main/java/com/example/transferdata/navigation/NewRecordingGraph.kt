package com.example.transferdata.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.transferdata.MainViewModel
import com.example.transferdata.common.utils.JsonHandler
import com.example.transferdata.navigation.NewRecordingNavGraphArgs.RECORDING_DESCRIPTION
import com.example.transferdata.navigation.NewRecordingNavGraphArgs.RECORDING_TITLE
import com.example.transferdata.navigation.NewRecordingNavGraphRoutes.CREATE_NEW_RECORDING
import com.example.transferdata.navigation.NewRecordingNavGraphRoutes.CREATE_NEW_RECORDING_GRAPH
import com.example.transferdata.navigation.NewRecordingNavGraphRoutes.RECORDING
import com.example.transferdata.presentation.createNewRecording.CreateNewRecording
import com.example.transferdata.presentation.recording.RecordingScreen

object NewRecordingNavGraphRoutes {
    const val CREATE_NEW_RECORDING_GRAPH = "create_new_recording_graph"
    const val CREATE_NEW_RECORDING = "create_new_recording"
    const val RECORDING = "recording"
}

object NewRecordingNavGraphArgs {
    const val RECORDING_TITLE = "recording_title"
    const val RECORDING_DESCRIPTION = "recording_description"
}

internal fun NavGraphBuilder.addNewRecordingGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    onBackPressed: () -> Unit,
    setKeepScreenFlag: (Boolean) -> Unit
) {
    navigation(
        route = CREATE_NEW_RECORDING_GRAPH,
        startDestination = CREATE_NEW_RECORDING
    ) {
        composable(CREATE_NEW_RECORDING) {
            CreateNewRecording(
                onBackPressed = onBackPressed,
                createdNewRecording = { title, description ->
                    val route = "$RECORDING?$RECORDING_TITLE=${
                        JsonHandler.getEncodedJsonParamAsUri(title)
                    }&$RECORDING_DESCRIPTION=${
                        JsonHandler.getEncodedJsonParamAsUri(description)
                    }"
                    navController.navigate(route
                    ) {
                        popUpTo(CREATE_NEW_RECORDING_GRAPH) {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable(
            route = "$RECORDING?${RECORDING_TITLE}={${RECORDING_TITLE}}&${RECORDING_DESCRIPTION}={${RECORDING_DESCRIPTION}}",
            arguments = listOf(
                navArgument(RECORDING_TITLE) { type = NavType.StringType },
                navArgument(RECORDING_DESCRIPTION) { type = NavType.StringType }
            )
        ) {
            RecordingScreen(
                mainViewModel = mainViewModel,
                onBackPressed = onBackPressed,
                setKeepScreenFlag = setKeepScreenFlag
            )
        }
    }
}