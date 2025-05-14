package com.example.transferdata.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.transferdata.navigation.MainNavGraphRoutes.HOME_GRAPH
import com.example.transferdata.navigation.MainNavGraphRoutes.HOME_SCREEN

object MainNavGraphRoutes {
    const val HOME_GRAPH = "home_graph"
    const val HOME_SCREEN = "home_screen"
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
        startDestination = HOME_GRAPH
    ) {
        addNewRecordingGraph(
            navController = navController,
            onBackPressed = onBackPressed,
            onClosePressed = onClosePressed,
        )
        navigation(
            route = HOME_GRAPH,
            startDestination = HOME_SCREEN
        ) {
            composable(HOME_SCREEN) {

            }
        }
    }
}