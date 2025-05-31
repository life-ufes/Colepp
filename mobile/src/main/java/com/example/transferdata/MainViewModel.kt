package com.example.transferdata

import android.bluetooth.BluetoothAdapter
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commons.AccelerometerData
import com.example.commons.Capabilities.Companion.ACCELEROMETER_CAPABILITY
import com.example.commons.Capabilities.Companion.WEAR_CAPABILITY
import com.example.commons.CommunicationPaths.Companion.ACCELEROMETER_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.INIT_ACCELEROMETER_TRANSFER_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.START_ACTIVITY_PATH
import com.example.commons.CommunicationPaths.Companion.STOP_ACCELEROMETER_TRANSFER_DATA_PATH
import com.example.transferdata.bluetoothHandler.BluetoothStateListener
import com.example.transferdata.common.utils.DevicesStatus
import com.example.transferdata.common.utils.RecordingStatus
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarSensorSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val polarBleApi: PolarBleApi,
    private val messageClient: MessageClient,
) : ViewModel(),
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener,
    BluetoothStateListener {

    init {
        viewModelScope.launch {
            delay(3000L)// gambiarra para poder coletar os valores dos flows apos eles instanciarem
            recordingStatusHandler()
            observerPreparing()
        }
    }

    //Smartwatch infos
    private val _capabilityInfos = MutableStateFlow<Map<String, Set<Node>>>(emptyMap())
    val capabilityInfos = _capabilityInfos.asStateFlow()

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "Message received: ${messageEvent.path} with ${messageEvent.data}")
        if (messageEvent.path == ACCELEROMETER_DATA_PATH) {
            val data = messageEvent.data
            if (data.isNotEmpty()) {
                val accelerometerData = AccelerometerData.fromByteArray(data)
                Log.d(TAG, "Accelerometer data received: $accelerometerData")
                // temporario para teste
                _wearSamples.value += Pair(
                    accelerometerData,
                    System.currentTimeMillis()
                )
                if (_timeOfFirstWearSample.value == null) {
                    _timeOfFirstWearSample.value = System.currentTimeMillis()
                }
            } else {
                Log.d(TAG, "No accelerometer data received")
            }
        }
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        try {
            updateCapabilityInfo(
                mapOf(
                    capabilityInfo.name to capabilityInfo.nodes
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d(TAG, "Capability values: ${_capabilityInfos.value}")
    }

    fun updateCapabilityInfo(infos: Map<String, Set<Node>>) {
        Log.d(TAG, "Updating capability info: $infos")
        Log.d(TAG, "Current capability infos: ${_capabilityInfos.value}")
        val updatedInfos = _capabilityInfos.value.toMutableMap()
        infos.forEach { (key, values) ->
            updatedInfos[key] = values
        }
        _capabilityInfos.value = updatedInfos
    }

    fun sendStarWearAppMessage() {
        capabilityInfos.value[WEAR_CAPABILITY]?.let { nodes ->
            Log.d(TAG, "Sending message to start wear app to nodes: $nodes")
            nodes.forEach { node ->
                messageClient.sendMessage(
                    node.id,
                    START_ACTIVITY_PATH,
                    null
                )
            }
        }
    }

    private fun sendStartAccelerometerMessage() {
        capabilityInfos.value[ACCELEROMETER_CAPABILITY]?.let { nodes ->
            Log.d(TAG, "Sending message to start accelerometer to nodes: $nodes")
            nodes.forEach { node ->
                messageClient.sendMessage(
                    node.id,
                    INIT_ACCELEROMETER_TRANSFER_DATA_PATH,
                    null
                )
            }
        }
    }

    private fun sendStopAccelerometerMessage() {
        capabilityInfos.value[ACCELEROMETER_CAPABILITY]?.let { nodes ->
            Log.d(TAG, "Sending message to stop accelerometer to nodes: $nodes")
            nodes.forEach { node ->
                messageClient.sendMessage(
                    node.id,
                    STOP_ACCELEROMETER_TRANSFER_DATA_PATH,
                    null
                )
            }
        }
    }

    //Bluetooth infos
    private val _bluetoothEnable = MutableStateFlow(false)
    val bluetoothEnable = _bluetoothEnable.asStateFlow()

    fun checkBluetoothState(adapter: BluetoothAdapter) {
        _bluetoothEnable.value = adapter.isEnabled == true
    }

    override fun onBluetoothStateChanged(state: Int) {
        Log.d(TAG, "Bluetooth state changed: $state")
        when (state) {
            BluetoothAdapter.STATE_OFF -> {
                Log.d(TAG, "Bluetooth is OFF")
                _bluetoothEnable.value = false
            }

            BluetoothAdapter.STATE_ON -> {
                Log.d(TAG, "Bluetooth is ON")
                _bluetoothEnable.value = true
            }
        }
    }

    //Polar Infos
    private var hrDisposable: Disposable? = null
    private var autoConnectDisposable: Disposable? = null

    private var _deviceId = MutableStateFlow<String?>(null)
    val deviceId = _deviceId.asStateFlow()
    private var _polarIsConnected = MutableStateFlow(false)
    val polarIsConnected = _polarIsConnected.asStateFlow()

    private val _hrValue = MutableStateFlow<Int?>(null)
    val hrValue = _hrValue.asStateFlow()

    fun polarConnect() {
        if (polarIsConnected.value) {
            Log.d(TAG, "Already connected to Polar device")
            return
        }
        polarBleApi.setApiCallback(myCallback)
        if (autoConnectDisposable != null && !autoConnectDisposable!!.isDisposed) {
            Log.d(TAG, "Auto connect already in progress")
            return
        }
        Log.d(TAG, "Starting auto connect to Polar device")
        autoConnectDisposable = polarBleApi.autoConnectToDevice(-60, "180D", null)
            .subscribe(
                { Log.d(TAG, "auto connect search complete") },
                { throwable: Throwable -> Log.e(TAG, "Error on auto connect", throwable) }
            )
    }

    private fun polarInitHrListener() {
        val isDisposed = hrDisposable?.isDisposed ?: true
        if (!isDisposed) {
            hrDisposable?.dispose()
        }
        deviceId.value?.let {
            hrDisposable = polarBleApi.startHrStreaming(it)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { hrData: PolarHrData ->
                        hrData.samples.firstOrNull()?.let { sample ->
                            _hrValue.value = sample.hr
                            _polarSamples.value += Pair(sample.hr, System.currentTimeMillis())
                            Log.d(TAG, "HR value received: ${sample.hr}")

                            // temporario para teste
                            if (_timeOfFirstPolarSample.value == null) {
                                _timeOfFirstPolarSample.value = System.currentTimeMillis()
                            }
                        }
                    },
                    { throwable: Throwable ->
                        Log.e(TAG, "Error receiving HR data", throwable)
                    }
                )
        }
    }

    private fun polarStopHrListener() {
        _hrValue.value = null
        hrDisposable?.dispose()
    }

    fun polarDisconnect() {
        deviceId.value?.let { polarBleApi.disconnectFromDevice(it) }
    }

    private val myCallback = object : PolarBleApiCallback() {
        override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
            Log.d(TAG, "CONNECTED: ${polarDeviceInfo.deviceId}")
            _deviceId.value = polarDeviceInfo.deviceId
            _polarIsConnected.value = true
        }

        override fun deviceConnecting(polarDeviceInfo: PolarDeviceInfo) {
            Log.d(TAG, "CONNECTING: ${polarDeviceInfo.deviceId}")
        }

        override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
            Log.d(TAG, "DISCONNECTED: ${polarDeviceInfo.deviceId}")
            _deviceId.value = null
            _polarIsConnected.value = false
        }

        override fun batteryLevelReceived(identifier: String, level: Int) {
            Log.d(TAG, "BATTERY LEVEL: $level")
        }
    }

    fun polarForegroundEntered() {
        polarBleApi.foregroundEntered()
    }

    fun polarShutDown() {
        polarBleApi.shutDown()
    }

    //Recording Status
    val devicesStatus: StateFlow<DevicesStatus> = combine(
        _bluetoothEnable,
        _polarIsConnected,
        _capabilityInfos
    ) { bluetoothEnable, deviceConnected, capabilityInfos ->
        Log.d(
            TAG,
            "combine: bluetoothEnable: $bluetoothEnable, deviceConnected: $deviceConnected, capabilityInfos: $capabilityInfos"
        )
        DevicesStatus.getRecordingStatus(
            bluetoothState = bluetoothEnable,
            polarState = deviceConnected,
            wearCapabilities = capabilityInfos
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = DevicesStatus.BluetoothOff
    )

    private val _recordingStatus = MutableStateFlow<RecordingStatus>(RecordingStatus.NotReady)
    val recordingStatus = _recordingStatus.asStateFlow()

    val buttonRecordingEnabled = _recordingStatus
        .map { status ->
            when (status) {
                is RecordingStatus.Ready, RecordingStatus.Running -> true
                else -> false
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    fun recordingButton() {
        if (_recordingStatus.value == RecordingStatus.Ready) {
            sendStartAccelerometerMessage()
            polarInitHrListener()
            _recordingStatus.value = RecordingStatus.Preparing

            //temporario para teste
            _initTime.value = System.currentTimeMillis()
        }
        if (_recordingStatus.value == RecordingStatus.Running) {
            sendStopAccelerometerMessage()
            polarStopHrListener()
            _recordingStatus.value = RecordingStatus.Finished
        }
    }

    private fun recordingStatusHandler() {
        viewModelScope.launch {
            devicesStatus.distinctUntilChanged { old, new -> old == new }.collect {
                Log.d(TAG, "Devices status collected: $it")
                when (it) {
                    is DevicesStatus.ReadyToRecord -> {
                        if (_recordingStatus.value == RecordingStatus.NotReady) {
                            _recordingStatus.value = RecordingStatus.Ready
                        }
                    }

                    else -> {
                        when (_recordingStatus.value) {
                            RecordingStatus.Ready -> {
                                _recordingStatus.value = RecordingStatus.NotReady
                            }

                            is RecordingStatus.Preparing, RecordingStatus.Running -> {
                                _recordingStatus.value = RecordingStatus.Error
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun observerPreparing() {
        viewModelScope.launch {
            Log.d(TAG, "Observing preparing status")
            recordingStatus.distinctUntilChanged { old, new -> old == new }.collect { status ->
                Log.d(TAG, "Recording status collected: $status")
                when (status) {
                    is RecordingStatus.Preparing -> {
                        startCountdown()
                    }
                    is RecordingStatus.Running -> {
                        startChronometer()
                    }
                    is RecordingStatus.Finished, RecordingStatus.Error -> {

                        val mediaOfTimesHR = _polarSamples.value
                            .map { it.second }
                            .mapIndexed { index, time ->
                                if(index==0) {
                                    0L
                                }else{
                                    time - _polarSamples.value[index - 1].second
                                }
                            }
                            .drop(0)
                            .average()

                        val mediaOfTimesWear = _wearSamples.value
                            .map { it.second }
                            .mapIndexed { index, time ->
                                if(index==0) {
                                    0L
                                }else{
                                    time - _wearSamples.value[index - 1].second
                                }
                            }
                            .drop(0)
                            .average()

                        val wearSorted = _wearSamples.value.sortedBy { it.first.timestamp }
                        val mediaOfTimeCollectWear = wearSorted
                            .mapIndexed { index, data ->
                                if (index == 0) {
                                    0L
                                } else {
                                    data.first.timestamp - wearSorted[index - 1].first.timestamp
                                }
                            }
                            .drop(0)
                            .average()

                        Log.d(TAG, "Average time between HR samples: $mediaOfTimesHR ms ${1000/mediaOfTimesHR} Hz")
                        Log.d(TAG, "Average time between Wear samples: $mediaOfTimesWear ms ${1000/mediaOfTimesWear} Hz")
                        Log.d(TAG, "Average time between Wear data collection: $mediaOfTimeCollectWear ns ${1_000_000_000/mediaOfTimeCollectWear} Hz")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun startCountdown() {
        Log.d(TAG, "Starting countdown")
        viewModelScope.launch {
            for (i in 3 downTo 1) {
                _preparingTime.value = i.toLong()
                Log.d(TAG, "Preparing time: ${_preparingTime.value}")
                delay(1000L)
            }
            _preparingTime.value = 0L
            _recordingStatus.value = RecordingStatus.Running
        }
    }

    private val _preparingTime = MutableStateFlow(INITIAL_TIME_OF_PREPARING)
    val preparingTime = _preparingTime.asStateFlow()

    private val _chronometer = MutableStateFlow(0L)
    val chronometer = _chronometer.asStateFlow()

    private fun startChronometer() {
        viewModelScope.launch {
            _chronometer.value = 0L
            val startTime = SystemClock.elapsedRealtime()

            while (_recordingStatus.value == RecordingStatus.Running) {
                val elapsed = SystemClock.elapsedRealtime() - startTime
                _chronometer.value = elapsed
                delay(TIME_BETWEEN_CHRONOMETER_UPDATES)
            }
        }
    }

    fun onScreenDestroy() {
        Log.d(TAG, "onScreenDestroy called")
        polarStopHrListener()
        sendStopAccelerometerMessage()
        _chronometer.value = 0L

        if(devicesStatus.value is DevicesStatus.ReadyToRecord) {
            _recordingStatus.value = RecordingStatus.Ready
        } else {
            _recordingStatus.value = RecordingStatus.NotReady
        }

        _initTime.value = null
        _timeOfFirstWearSample.value = null
        _timeOfFirstPolarSample.value = null
        _polarSamples.value = emptyList()
        _wearSamples.value = emptyList()
    }

    // temporario para teste
    private val _initTime = MutableStateFlow<Long?>(null)
    val initTime = _initTime.asStateFlow()

    private val _timeOfFirstPolarSample = MutableStateFlow<Long?>(null)
    val timeOfFirstPolarSample = _timeOfFirstPolarSample.asStateFlow()

    private val _polarSamples = MutableStateFlow<List<Pair<Int, Long>>>(emptyList())
    val polarSamples = _polarSamples.asStateFlow()

    private val _timeOfFirstWearSample = MutableStateFlow<Long?>(null)
    val timeOfFirstWearSample = _timeOfFirstWearSample.asStateFlow()

    private val _wearSamples = MutableStateFlow<List<Pair<AccelerometerData, Long>>>(emptyList())
    val wearSamples = _wearSamples.asStateFlow()

    companion object {
        private const val TAG = "MainViewModel"
        private const val INITIAL_TIME_OF_PREPARING = 3L
        private const val TIME_BETWEEN_CHRONOMETER_UPDATES = 10L
    }
}