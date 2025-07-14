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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.transferdata.MainViewModel
import com.example.transferdata.R
import com.example.transferdata.common.composeUI.CardOfWearable
import com.example.transferdata.common.composeUI.DefaultButton
import com.example.transferdata.common.composeUI.Toolbar
import com.example.transferdata.common.composeUI.arrangementLastItemOnBottom
import com.example.transferdata.common.utils.DevicesStatus
import com.example.transferdata.common.utils.RecordingStatus
import com.example.transferdata.common.utils.Size
import com.example.transferdata.common.utils.TextStyles
import com.example.transferdata.common.utils.orDefault
import com.example.transferdata.common.utils.toCronometerFormat
import com.google.android.gms.wearable.Node
import com.polar.sdk.api.model.PolarDeviceInfo

@Composable
fun RecordingScreen(
    viewModel: RecordingViewModel = hiltViewModel(),
    mainViewModel: MainViewModel,
    onBackPressed: () -> Unit,
    setKeepScreenFlag: (Boolean) -> Unit
) {
    val devicesStatus = mainViewModel.devicesStatus.collectAsState()
    val buttonEnabled = mainViewModel.buttonRecordingEnabled.collectAsState()
    val recordingStatus = mainViewModel.recordingStatus.collectAsState()
    val preparingTime = mainViewModel.preparingTime.collectAsState()
    val chronometerTime = mainViewModel.chronometer.collectAsState()
    val hrPolarValue = mainViewModel.polarStatus.hrValue.collectAsState()
    val hrSmartwatchValue = mainViewModel.wearableStatus.hrValue.collectAsState()
    val polarInfo = mainViewModel.polarStatus.device.collectAsState()
    val polarBatteryLevel = mainViewModel.polarStatus.batteryLevel.collectAsState()
    val wearableInfo = mainViewModel.wearableStatus.wearableNode.collectAsState()

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
                heartRatePolar = hrPolarValue.value?.first,
                heartRateSmartwatch = hrSmartwatchValue.value?.heartRate,
                recordingButtonClick = {
                    mainViewModel.recordingButton(
                        viewModel.recordTitle,
                        viewModel.recordDescription
                    )
                },
                devicesStatus = devicesStatus.value,
                buttonEnabled = buttonEnabled.value,
                polarInfo = polarInfo.value,
                polarBatteryLevel = polarBatteryLevel.value,
                wearableInfo = wearableInfo.value,
                sendMessageToStartWearApp = mainViewModel.wearableStatus::sendStarWearAppMessage,
                connectOnPolar = mainViewModel.polarStatus::connect
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
    heartRatePolar: Int?,
    heartRateSmartwatch: Int?,
    recordingButtonClick: () -> Unit,
    devicesStatus: DevicesStatus,
    sendMessageToStartWearApp: () -> Unit,
    connectOnPolar: () -> Unit,
    polarInfo: PolarDeviceInfo?,
    polarBatteryLevel: Int?,
    wearableInfo: Node?,
    buttonEnabled: Boolean,
) {
    val scroll = rememberScrollState()
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Size.size05)
                .padding(bottom = Size.size09),
            verticalArrangement = arrangementLastItemOnBottom
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scroll),
                verticalArrangement = Arrangement.spacedBy(Size.size03),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = chronometerTime.toCronometerFormat(),
                    style = TextStyles.Chronometer
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Size.size05),
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        if (heartRateSmartwatch == null && heartRatePolar == null) {
                            Text(
                                text = stringResource(R.string.no_heart_rate),
                                style = TextStyles.TextL
                            )
                        }
                        if (heartRatePolar != null) {
                            Text(
                                text = stringResource(R.string.polar_heart_rate, heartRatePolar),
                                style = TextStyles.TextL
                            )
                        }
                        if (heartRateSmartwatch != null) {
                            Text(
                                text = stringResource(
                                    R.string.smart_watch_heart_rate,
                                    heartRateSmartwatch
                                ),
                                style = TextStyles.TextL
                            )
                        }
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_heart),
                        contentDescription = null,
                    )
                }
                CardOfSmartWatch(
                    modifier = Modifier.fillMaxWidth(),
                    devicesStatus = devicesStatus,
                    wearableInfo = wearableInfo,
                    sendMessageToStartWearApp = sendMessageToStartWearApp,
                )
                CardOfPolar(
                    modifier = Modifier.fillMaxWidth(),
                    devicesStatus = devicesStatus,
                    polarInfo = polarInfo,
                    batteryLevel = polarBatteryLevel,
                    connectOnPolar = connectOnPolar
                )
            }
            DefaultButton(
                text = stringResource(
                    if (recordingStatus is RecordingStatus.Ready) {
                        R.string.start_recording
                    } else {
                        R.string.finish_recording
                    }
                ),
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
            style = TextStyles.Chronometer
        )
    }
}

@Composable
private fun CardOfSmartWatch(
    modifier: Modifier = Modifier,
    devicesStatus: DevicesStatus,
    wearableInfo: Node?,
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
        title = stringResource(R.string.smart_watch, wearableInfo?.displayName.orDefault()),
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
    polarInfo: PolarDeviceInfo?,
    batteryLevel: Int?,
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
        title = stringResource(R.string.polar, polarInfo?.name.orDefault()),
        subtitle = subtitle,
        onClick = {
//            if (devicesStatus is DevicesStatus.OnlyBluetoothOn
//                || devicesStatus is DevicesStatus.OnlyWearOn
//                || devicesStatus is DevicesStatus.WearAndPolarOn
//            ) {
            connectOnPolar()
//            }
        },
    ) {
        batteryLevel?.let {
            Text(
                text = stringResource(R.string.battery_level, it),
                style = TextStyles.TextS
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenContentPreview() {
    ScreenContent(
        preparingTime = 1,
        heartRatePolar = 80,
        heartRateSmartwatch = 83,
        chronometerTime = 1234567L,
        recordingButtonClick = { },
        recordingStatus = RecordingStatus.Running,
        devicesStatus = DevicesStatus.ReadyToRecord,
        buttonEnabled = true,
        polarInfo = null,
        polarBatteryLevel = 60,
        wearableInfo = null,
        sendMessageToStartWearApp = { },
        connectOnPolar = { }
    )
}