package com.example.transferdata.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.transferdata.MainViewModel
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

internal fun NavGraphBuilder.addNewRecordingGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    onBackPressed: () -> Unit,
    onClosePressed: () -> Unit,
    setKeepScreenFlag: (Boolean) -> Unit
) {
    navigation(
        route = CREATE_NEW_RECORDING_GRAPH,
        startDestination = CREATE_NEW_RECORDING
    ) {
        composable(CREATE_NEW_RECORDING) {
            CreateNewRecording(
                mainViewModel = mainViewModel,
                onBackPressed = onBackPressed,
                createdNewRecording = {
                    navController.navigate(RECORDING) {
                        popUpTo(CREATE_NEW_RECORDING_GRAPH) {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable(RECORDING) {
            RecordingScreen(
                mainViewModel = mainViewModel,
                onBackPressed = onBackPressed,
                startRecording = { },
                stopRecording = { },
                setKeepScreenFlag = setKeepScreenFlag
            )
        }
    }
}