package com.example.colepp.navigation

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.colepp.common.utils.JsonHandler
import com.example.colepp.navigation.MainNavGraphArgs.RECORD_ID
import com.example.colepp.navigation.MainNavGraphRoutes.HOME_GRAPH
import com.example.colepp.navigation.MainNavGraphRoutes.HOME_SCREEN
import com.example.colepp.navigation.MainNavGraphRoutes.RECORD_DETAIL_SCREEN
import com.example.colepp.navigation.MainNavGraphRoutes.RECORD_EDIT_SCREEN
import com.example.colepp.navigation.NewRecordingNavGraphRoutes.CREATE_NEW_RECORDING_GRAPH
import com.example.colepp.presentation.home.HomeScreen
import com.example.colepp.presentation.recordDetail.RecordDetailScreen
import com.example.colepp.presentation.recordEdit.RecordEditScreen
import java.io.File

object MainNavGraphRoutes {
    const val HOME_GRAPH = "home_graph"
    const val HOME_SCREEN = "home_screen"
    const val RECORD_DETAIL_SCREEN = "record_detail_screen"
    const val RECORD_EDIT_SCREEN = "record_edit_screen"
}

object MainNavGraphArgs {
    const val RECORD_ID = "record_id"
}

internal fun NavGraphBuilder.addMainGraph(
    navController: NavHostController,
    createDatasetFile: (Long, (File) -> Unit) -> Unit,
    onBackPressed: () -> Unit,
    shareFile: (Uri) -> Unit
) {
    navigation(
        route = HOME_GRAPH,
        startDestination = HOME_SCREEN
    ) {
        composable(HOME_SCREEN) {
            HomeScreen(
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
                onEditClicked = { recordId ->
                    navController.navigate(
                        "$RECORD_EDIT_SCREEN/$RECORD_ID=${
                            JsonHandler.getEncodedJsonParamAsUri(recordId)
                        }"
                    )
                }
            )
        }
        composable("$RECORD_DETAIL_SCREEN/${RECORD_ID}={${RECORD_ID}}") {
            RecordDetailScreen(
                onBackPressed = onBackPressed,
                createDatasetFile = createDatasetFile,
                shareFile = shareFile,
            )
        }
        composable("$RECORD_EDIT_SCREEN/${RECORD_ID}={${RECORD_ID}}") {
            RecordEditScreen(
                onBackPressed = onBackPressed,
                onSave = {
                    navController.popBackStack(
                        route = HOME_SCREEN,
                        inclusive = false
                    )
                }
            )
        }
    }
}