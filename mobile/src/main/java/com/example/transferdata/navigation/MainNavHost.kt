package com.example.transferdata.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.transferdata.MainViewModel
import com.example.transferdata.navigation.MainNavGraphRoutes.HOME_GRAPH
import com.example.transferdata.navigation.MainNavGraphRoutes.HOME_SCREEN
import com.example.transferdata.navigation.NewRecordingNavGraphRoutes.CREATE_NEW_RECORDING_GRAPH
import com.example.transferdata.presentation.home.HomeScreen

object MainNavGraphRoutes {
    const val HOME_GRAPH = "home_graph"
    const val HOME_SCREEN = "home_screen"
}

@Composable
internal fun MainNavHost(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel,
    onBackPressed: () -> Unit,
    onClosePressed: () -> Unit,
    setKeepScreenFlag: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = HOME_GRAPH
    ) {
        addNewRecordingGraph(
            navController = navController,
            mainViewModel = mainViewModel,
            onBackPressed = onBackPressed,
            onClosePressed = onClosePressed,
            setKeepScreenFlag = setKeepScreenFlag
        )
        navigation(
            route = HOME_GRAPH,
            startDestination = HOME_SCREEN
        ) {
            composable(HOME_SCREEN) {
                HomeScreen(
                    mainViewModel = mainViewModel,
                    onRecordingPressed = {},
                    createNewRecording = {
                        navController.navigate(
                            CREATE_NEW_RECORDING_GRAPH
                        )
                    }
                )
            }
        }
    }
}