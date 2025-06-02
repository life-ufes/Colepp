package com.example.transferdata.presentation.recording

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.commons.AccelerometerData
import com.example.transferdata.MainViewModel
import com.example.transferdata.R
import com.example.transferdata.common.composeUI.CardOfWearable
import com.example.transferdata.common.composeUI.DefaultButton
import com.example.transferdata.common.composeUI.Toolbar
import com.example.transferdata.common.utils.DevicesStatus
import com.example.transferdata.common.utils.RecordingStatus
import com.example.transferdata.common.utils.Size
import java.util.Calendar

@Composable
fun RecordingScreen(
    viewModel: RecordingViewModel = hiltViewModel(),
    mainViewModel: MainViewModel,
    onBackPressed: () -> Unit,
    startRecording: () -> Unit,
    stopRecording: () -> Unit,
    setKeepScreenFlag: (Boolean) -> Unit
) {
    val devicesStatus = mainViewModel.devicesStatus.collectAsState()
    val buttonEnabled = mainViewModel.buttonRecordingEnabled.collectAsState()
    val recordingStatus = mainViewModel.recordingStatus.collectAsState()
    val preparingTime = mainViewModel.preparingTime.collectAsState()
    val chronometerTime = mainViewModel.chronometer.collectAsState()
    val hrValue = mainViewModel.hrValue.collectAsState()

    val initialTime = mainViewModel.initTime.collectAsState()
    val initialHeartRateTime = mainViewModel.timeOfFirstPolarSample.collectAsState()
    val initialWearTime = mainViewModel.timeOfFirstWearSample.collectAsState()
    val polarSamples = mainViewModel.polarSamples.collectAsState()
    val wearSamples = mainViewModel.wearSamples.collectAsState()


    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Log.d("RecordingScreen", "Lifecycle ON_CREATE")
                }

                Lifecycle.Event.ON_STOP -> {
                    Log.d("RecordingScreen", "Lifecycle ON_STOP")
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            Log.d("RecordingScreen", "Lifecycle ON_DESTROY")
            mainViewModel.onScreenDestroy()
        }
    }

    LaunchedEffect(recordingStatus.value) {
        when (recordingStatus.value) {
            is RecordingStatus.Preparing -> setKeepScreenFlag(true)
            is RecordingStatus.Finished, RecordingStatus.Error -> setKeepScreenFlag(false)
            else -> {}
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Toolbar(
                title = stringResource(R.string.new_recording),
                onBackPressed = onBackPressed,
                hasCloseIcon = false
            )
            ScreenContent(
                recordingStatus = recordingStatus.value,
                preparingTime = preparingTime.value,
                chronometerTime = chronometerTime.value,
                heartRate = hrValue.value,
                recordingButtonClick = mainViewModel::recordingButton,
                devicesStatus = devicesStatus.value,
                buttonEnabled = buttonEnabled.value,
                sendMessageToStartWearApp = {
                    mainViewModel.sendStarWearAppMessage()
                },
                connectOnPolar = mainViewModel::polarConnect,

                // para teste
                initialTime = initialTime.value,
                initialHeartRateTime = initialHeartRateTime.value,
                initialWearTime = initialWearTime.value,
                polarSamples = polarSamples.value,
                wearSamples = wearSamples.value
            )
        }
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    recordingStatus: RecordingStatus,
    preparingTime: Long,
    chronometerTime: Long,
    heartRate: Int?,
    recordingButtonClick: () -> Unit,
    devicesStatus: DevicesStatus,
    sendMessageToStartWearApp: () -> Unit,
    connectOnPolar: () -> Unit,
    buttonEnabled: Boolean,

    // para teste
    initialTime: Long?,
    initialHeartRateTime: Long?,
    initialWearTime: Long?,
    polarSamples: List<Pair<Int, Long>>,
    wearSamples: List<Pair<AccelerometerData, Long>>
) {
    val scroll = rememberScrollState()
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = Size.size05),
            verticalArrangement = Arrangement.spacedBy(Size.size03),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = chronometerTime.toCronometerFormat(),
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
                    text = heartRate?.toString() ?: "-",
                    fontSize = 40.sp,
                )
            }
//            Spacer(modifier = Modifier.weight(1f))
            // Temporario para teste
            DataScreen(
                initialTime = initialTime,
                initialHeartRateTime = initialHeartRateTime,
                initialWearTime = initialWearTime,
                polarSamples = polarSamples,
                wearSamples = wearSamples
            )
            CardOfSmartWatch(
                modifier = Modifier.fillMaxWidth(),
                devicesStatus = devicesStatus,
                sendMessageToStartWearApp = sendMessageToStartWearApp,
            )
            CardOfPolar(
                modifier = Modifier.fillMaxWidth(),
                devicesStatus = devicesStatus,
                connectOnPolar = connectOnPolar
            )
            DefaultButton(
                text = stringResource(R.string.start_recording),
                onClick = {
                    recordingButtonClick()
                },
                enabled = buttonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Size.size05),
            )
        }
        AnimatedVisibility(
            visible = recordingStatus is RecordingStatus.Preparing,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            PreparingCountScreen(
                modifier = Modifier.fillMaxSize(),
                time = preparingTime
            )
        }
    }
}

//temporario
@Composable
private fun DataScreen(
    initialTime: Long?,
    initialHeartRateTime: Long?,
    initialWearTime: Long?,
    polarSamples: List<Pair<Int, Long>>,
    wearSamples: List<Pair<AccelerometerData, Long>>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Initial Time: ${initialTime?.toShow() ?: "N/A"}")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Initial Heart Rate Time: ${initialHeartRateTime?.toShow() ?: "N/A"}",
                modifier = Modifier
                    .weight(1f)
            )
            Text(
                text = "Initial Wear Time: ${initialWearTime?.toShow() ?: "N/A"}",
                modifier = Modifier
                    .weight(1f)
            )

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(Size.size02, Alignment.Top),
            ) {
                polarSamples.takeLast(15).forEach { sample ->
                    Text(text = "HR: ${sample.first}, T: ${sample.second.toShow()}")
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(Size.size02, Alignment.Top),
            ) {
                wearSamples.takeLast(2).forEach { sample ->
                    Text(text = "data: ${sample.first} T: ${sample.second.toShow()}")
                }
            }
        }
    }
}
private fun Long.toShow(): String {
    Calendar.getInstance().apply {
        timeInMillis = this@toShow
    }.let { calendar ->
        return String.format(
            "%02d:%02d:%04d",
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND),
            calendar.get(Calendar.MILLISECOND)
        )
    }
}

@Composable
private fun PreparingCountScreen(
    modifier: Modifier = Modifier,
    time: Long,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.gray_50).copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time.toString(),
            fontSize = 40.sp
        )
    }
}

@Composable
private fun CardOfSmartWatch(
    modifier: Modifier = Modifier,
    devicesStatus: DevicesStatus,
    sendMessageToStartWearApp: () -> Unit,
) {
    Log.d("TAG", "CardOfSmartWatch: $devicesStatus")
    val subtitle = when (devicesStatus) {
        is DevicesStatus.BluetoothOff -> stringResource(R.string.bluetooth_off)
        is DevicesStatus.OnlyBluetoothOn, DevicesStatus.OnlyPolarOn -> stringResource(R.string.connect_devices_to_transfer_data)
        is DevicesStatus.OnlyWearOn, DevicesStatus.WearAndPolarOn -> stringResource(R.string.only_wear_on)
        is DevicesStatus.WearOnWithTransferCapability, DevicesStatus.ReadyToRecord -> stringResource(
            R.string.ready_to_record
        )
    }
    CardOfWearable(
        modifier = modifier,
        title = stringResource(R.string.smart_watch),
        subtitle = subtitle,
        onClick = {
            if (devicesStatus is DevicesStatus.OnlyWearOn || devicesStatus is DevicesStatus.WearAndPolarOn) {
                sendMessageToStartWearApp()
            }
        },
    )
}

@Composable
private fun CardOfPolar(
    modifier: Modifier = Modifier,
    devicesStatus: DevicesStatus,
    connectOnPolar: () -> Unit
) {
    val bluetoothState = (devicesStatus.code and DevicesStatus.BLUETOOTH_BIT) != 0
    val polarState = (devicesStatus.code and DevicesStatus.POLAR_BIT) != 0
    val subtitle = when {
        !bluetoothState -> stringResource(R.string.bluetooth_off)
        bluetoothState && !polarState -> stringResource(R.string.connect_devices_to_transfer_data)
        else -> stringResource(R.string.ready_to_record)
    }
    CardOfWearable(
        modifier = modifier,
        title = stringResource(R.string.polar),
        subtitle = subtitle,
        onClick = {
//            if (devicesStatus is DevicesStatus.OnlyBluetoothOn
//                || devicesStatus is DevicesStatus.OnlyWearOn
//                || devicesStatus is DevicesStatus.WearAndPolarOn
//            ) {
            connectOnPolar()
//            }
        },
    )
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
        preparingTime = 1,
        heartRate = 80,
        chronometerTime = 1234567L,
        recordingButtonClick = { },
        recordingStatus = RecordingStatus.Running,
        devicesStatus = DevicesStatus.ReadyToRecord,
        buttonEnabled = true,
        sendMessageToStartWearApp = { },
        connectOnPolar = { }

        // para teste
        , initialTime = 123123123L,
        initialHeartRateTime = 123123123L,
        initialWearTime = 123123123L,
        polarSamples = listOf(
            Pair(80, 123123123L),
            Pair(82, 123123124L),
            Pair(85, 123123125L)
        ),
        wearSamples = listOf(
            Pair(AccelerometerData(0f, 0f, 0f, 123123123L), 123123123L),
            Pair(AccelerometerData(1f, 1f, 1f, 123123124L), 123123124L),
            Pair(AccelerometerData(2f, 2f, 2f, 123123125L), 123123125L)
        )
    )
}