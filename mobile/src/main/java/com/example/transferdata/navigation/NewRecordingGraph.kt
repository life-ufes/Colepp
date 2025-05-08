package com.example.transferdata.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.transferdata.navigation.MainNavGraphRoutes.CREATE_NEW_RECORDING
import com.example.transferdata.navigation.MainNavGraphRoutes.CREATE_NEW_RECORDING_GRAPH
import com.example.transferdata.navigation.MainNavGraphRoutes.RECORDING
import com.example.transferdata.presentation.createNewRecording.CreateNewRecording
import com.example.transferdata.presentation.recording.RecordingScreen

internal fun NavGraphBuilder.addNewRecordingGraph(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    onClosePressed: () -> Unit,
    sendCount: (Int) -> Unit,
    apiAvailable: Boolean,
) {
    navigation(
        route = CREATE_NEW_RECORDING_GRAPH,
        startDestination = CREATE_NEW_RECORDING
    ) {
        composable(CREATE_NEW_RECORDING) {
            CreateNewRecording(
                onBackPressed = onBackPressed,
                createdNewRecording = {
                    navController.navigate(RECORDING) {
                        popUpTo(CREATE_NEW_RECORDING_GRAPH) {
                            inclusive = true
                        }
                    }
                },
                apiAvailable = apiAvailable,
            )
        }
        composable(RECORDING) {
            RecordingScreen(
                onBackPressed = onBackPressed,
                startRecording = { },
                stopRecording = { },
            )
        }
    }
}