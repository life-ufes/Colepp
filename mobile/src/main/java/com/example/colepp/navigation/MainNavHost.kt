package com.example.colepp.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.colepp.MainViewModel
import com.example.colepp.navigation.MainNavGraphRoutes.HOME_GRAPH
import java.io.File

@Composable
internal fun MainNavHost(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel,
    onBackPressed: () -> Unit,
    setKeepScreenFlag: (Boolean) -> Unit,
    createDatasetFile: (Long, (File) -> Unit) -> Unit,
    shareFile: (Uri) -> Unit,
    requestBluetoothScanPermission: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = HOME_GRAPH
    ) {
        addMainGraph(
            navController = navController,
            onBackPressed = onBackPressed,
            createDatasetFile = createDatasetFile,
            shareFile = shareFile
        )
        addNewRecordingGraph(
            navController = navController,
            mainViewModel = mainViewModel,
            onBackPressed = onBackPressed,
            setKeepScreenFlag = setKeepScreenFlag,
            requestBluetoothScanPermission = requestBluetoothScanPermission
        )
    }
}