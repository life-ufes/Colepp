package com.example.transferdata.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.transferdata.MainViewModel
import com.example.transferdata.navigation.MainNavGraphRoutes.HOME_GRAPH

@Composable
internal fun MainNavHost(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel,
    onBackPressed: () -> Unit,
    onClosePressed: () -> Unit,
    setKeepScreenFlag: (Boolean) -> Unit,
    createDatasetFile: (Long) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = HOME_GRAPH
    ) {
        addMainGraph(
            navController = navController,
            onBackPressed = onBackPressed,
            onClosePressed = onClosePressed,
            createDatasetFile = createDatasetFile
        )
        addNewRecordingGraph(
            navController = navController,
            mainViewModel = mainViewModel,
            onBackPressed = onBackPressed,
            onClosePressed = onClosePressed,
            setKeepScreenFlag = setKeepScreenFlag
        )
    }
}