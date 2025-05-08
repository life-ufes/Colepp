package com.example.transferdata.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.transferdata.navigation.MainNavGraphRoutes.CREATE_NEW_RECORDING_GRAPH

object MainNavGraphRoutes {
    const val CREATE_NEW_RECORDING_GRAPH = "create_new_recording_graph"
    const val CREATE_NEW_RECORDING = "create_new_recording"
    const val RECORDING = "recording"
}

@Composable
internal fun MainNavHost(
    navController: NavHostController = rememberNavController(),
    onBackPressed: () -> Unit,
    onClosePressed: () -> Unit,
    sendCount: (Int) -> Unit,
    apiAvailable: Boolean,
) {
    NavHost(
        navController = navController,
        startDestination = CREATE_NEW_RECORDING_GRAPH
    ) {
        addNewRecordingGraph(
            navController = navController,
            onBackPressed = onBackPressed,
            onClosePressed = onClosePressed,
            sendCount = sendCount,
            apiAvailable = apiAvailable
        )
    }
}