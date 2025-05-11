package com.example.transferdata.presentation.recording

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transferdata.R
import com.example.transferdata.polarHandler.PolarBleApiSingleton
import com.example.transferdata.common.composeUI.DefaultButton
import com.example.transferdata.common.composeUI.Toolbar
import com.example.transferdata.common.utils.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RecordingScreen(
    viewModel: RecordingViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    startRecording: () -> Unit,
    stopRecording: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            viewModel.setPolarApi(
                PolarBleApiSingleton.getInstance(context)
            )
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Size.size03)
        ) {
            Toolbar(
                title = stringResource(R.string.new_recording),
                onBackPressed = onBackPressed,
                hasCloseIcon = false
            )
            ScreenContent(
                time = 100000L,
                heartRate = 80,
                startRecording = startRecording,
            )
        }
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    time: Long,
    heartRate: Int,
    startRecording: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Size.size03),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = time.toCronometerFormat(),
            fontSize = 40.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Size.size05),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_heart),
                contentDescription = null,
            )
            Text(
                text = heartRate.toString(),
                fontSize = 40.sp,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        DefaultButton(
            text = stringResource(R.string.start_recording),
            onClick = {
                startRecording()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(Size.size05)
        )
    }
}

private fun Long.toCronometerFormat(): String {
    val miliSeconds = this % 1000 / 100
    val seconds = this / 1000
    val minutes = seconds / 60
    return String.format(
        "%02d:%02d.%d",
        minutes % 60,
        seconds % 60,
        miliSeconds
    )
}

@Preview(showBackground = true)
@Composable
private fun ScreenContentPreview() {
    ScreenContent(
        time = 100000L,
        heartRate = 80,
        startRecording = { },
    )
}