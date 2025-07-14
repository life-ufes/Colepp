package com.example.transferdata

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transferdata.bluetoothHandler.BluetoothStatus
import com.example.transferdata.common.utils.DevicesStatus
import com.example.transferdata.common.utils.RecordingStatus
import com.example.transferdata.database.model.AccelerometerPolarEntity
import com.example.transferdata.database.model.AccelerometerSmartwatchEntity
import com.example.transferdata.database.model.AmbientTemperatureSmartwatchEntity
import com.example.transferdata.database.model.GravitySmartwatchEntity
import com.example.transferdata.database.model.GyroscopeSmartwatchEntity
import com.example.transferdata.database.model.HeartRatePolarEntity
import com.example.transferdata.database.model.HeartRateSmartwatchEntity
import com.example.transferdata.database.model.LinearAccelerationSmartwatchEntity
import com.example.transferdata.database.model.RecordEntity
import com.example.transferdata.database.repository.RecordDatabase
import com.example.transferdata.polarHandler.PolarStatus
import com.example.transferdata.wearableHandler.WearableStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val bluetoothStatus: BluetoothStatus,
    val polarStatus: PolarStatus,
    val wearableStatus: WearableStatus,
    private val recordDatabase: RecordDatabase
) : ViewModel() {

    init {
        viewModelScope.launch {
            delay(3000L)// gambiarra para poder coletar os valores dos flows apos eles instanciarem
            recordingStatusHandler()
            observerPreparing()
            observerClockSkew()
            observerWearSamples()
            observerPolarSamples()
        }
    }

    private fun observerPolarSamples() {
        viewModelScope.launch {
            polarStatus.hrValue.collectLatest { hr ->
                if (hr != null && currentRecordId.value != null) {
                    recordDatabase.heartRatePolarDao().insert(
                        HeartRatePolarEntity(
                            heartRate = hr.first,
                            timestamp = hr.second,
                            recordId = currentRecordId.value!!
                        )
                    )
                }
            }
        }
        viewModelScope.launch {
            polarStatus.accValue.collectLatest { acc ->
                if (acc != null && currentRecordId.value != null) {
                    recordDatabase.accelerometerPolarDao().insert(
                        AccelerometerPolarEntity(
                            x = acc.x,
                            y = acc.y,
                            z = acc.z,
                            timestamp = acc.timeStamp,
                            recordId = currentRecordId.value!!
                        )
                    )
                }
            }
        }
    }

    private fun observerWearSamples() {
        viewModelScope.launch {
            wearableStatus.accValue.collectLatest { acc ->
                if (acc != null && currentRecordId.value != null) {
                    recordDatabase.accelerometerSmartwatchDao().insert(
                        AccelerometerSmartwatchEntity(
                            x = acc.x,
                            y = acc.y,
                            z = acc.z,
                            timestamp = acc.timestamp,
                            recordId = currentRecordId.value!!
                        )
                    )
                }
            }
        }
        viewModelScope.launch {
            wearableStatus.linearAccValue.collectLatest { linearAcc ->
                if (linearAcc != null && currentRecordId.value != null) {
                    recordDatabase.linearAccelerationSmartwatchDao().insert(
                        LinearAccelerationSmartwatchEntity(
                            x = linearAcc.x,
                            y = linearAcc.y,
                            z = linearAcc.z,
                            timestamp = linearAcc.timestamp,
                            recordId = currentRecordId.value!!
                        )
                    )
                }
            }
        }
        viewModelScope.launch {
            wearableStatus.hrValue.collectLatest { hr ->
                if (hr != null && currentRecordId.value != null) {
                    recordDatabase.heartRateSmartwatchDao().insert(
                        HeartRateSmartwatchEntity(
                            heartRate = hr.heartRate,
                            timestamp = hr.timestamp,
                            recordId = currentRecordId.value!!
                        )
                    )
                }
            }
        }
        viewModelScope.launch {
            wearableStatus.gyroscopeValue.collectLatest { gyro ->
                if (gyro != null && currentRecordId.value != null) {
                    recordDatabase.gyroscopeSmartwatchDao().insert(
                        GyroscopeSmartwatchEntity(
                            x = gyro.x,
                            y = gyro.y,
                            z = gyro.z,
                            timestamp = gyro.timestamp,
                            recordId = currentRecordId.value!!
                        )
                    )
                }
            }
        }
        viewModelScope.launch {
            wearableStatus.ambientTemperatureValue.collectLatest { temp ->
                if (temp != null && currentRecordId.value != null) {
                    recordDatabase.ambientTemperatureSmartwatchDao().insert(
                        AmbientTemperatureSmartwatchEntity(
                            temperature = temp.temperature,
                            timestamp = temp.timestamp,
                            recordId = currentRecordId.value!!
                        )
                    )
                }
            }
        }
        viewModelScope.launch {
            wearableStatus.gravityValue.collectLatest { gravity ->
                if (gravity != null && currentRecordId.value != null) {
                    recordDatabase.gravitySmartwatchDao().insert(
                        GravitySmartwatchEntity(
                            x = gravity.x,
                            y = gravity.y,
                            z = gravity.z,
                            timestamp = gravity.timestamp,
                            recordId = currentRecordId.value!!
                        )
                    )
                }
            }
        }
    }

    private fun observerClockSkew() {
        viewModelScope.launch {
            wearableStatus.syncTimeValue.collectLatest { syncTime ->
                syncTime?.let { time ->
                    _currentRecordId.value?.let { recordId ->
                        recordDatabase.recordDao().setClockSkew(
                            id = recordId,
                            clockSkew = time
                        )
                    }
                }
            }
        }
    }

    private val _currentRecordId = MutableStateFlow<Long?>(null)
    val currentRecordId = _currentRecordId.asStateFlow()

    val devicesStatus: StateFlow<DevicesStatus> = combine(
        bluetoothStatus.bluetoothEnable,
        polarStatus.device,
        wearableStatus.capabilityInfos
    ) { bluetoothEnable, device, capabilityInfos ->
        Log.d(
            TAG,
            "combine: bluetoothEnable: $bluetoothEnable, device: $device, capabilityInfos: $capabilityInfos"
        )
        DevicesStatus.getRecordingStatus(
            bluetoothState = bluetoothEnable,
            polarState = device != null,
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

    fun recordingButton(title: String, description: String) {
        if (_recordingStatus.value == RecordingStatus.Ready) {
            wearableStatus.startTransferData()
            polarStatus.startListeners()
            _recordingStatus.value = RecordingStatus.Preparing
            viewModelScope.launch {
                try {
                    val clockSkew = wearableStatus.syncTimeValue.value ?: 0L
                    recordDatabase.recordDao().insert(
                        RecordEntity(
                            title = title,
                            description = description,
                            clockSkewSmartwatchNanos = clockSkew
                        )
                    ).run {
                        Log.d(TAG, "Record inserted with ID: $this")
                        _currentRecordId.value = this
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error inserting record: ${e.message}")
                    _recordingStatus.value = RecordingStatus.Error
                }
            }
        }
        if (_recordingStatus.value == RecordingStatus.Running) {
            wearableStatus.stopTransferData()
            polarStatus.stopListeners()
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
                        val timeNanos = SystemClock.elapsedRealtimeNanos()
                        val timeMillis = System.currentTimeMillis()
                        currentRecordId.value?.let { recordId ->
                            recordDatabase.recordDao().setStarRecordingTime(
                                id = recordId,
                                starRecordingNanos = timeNanos,
                                starRecordingMilli = timeMillis
                            )
                        }
                    }

                    is RecordingStatus.Finished, RecordingStatus.Error -> {
                        val timeNanos = SystemClock.elapsedRealtimeNanos()
                        val timeMillis = System.currentTimeMillis()
                        currentRecordId.value?.let { recordId ->
                            recordDatabase.recordDao().setStopRecordingTime(
                                id = recordId,
                                stopRecordingNanos = timeNanos,
                                stopRecordingMilli = timeMillis
                            )
                        }
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
        polarStatus.stopListeners()
        wearableStatus.stopTransferData()
        _chronometer.value = 0L

        if (devicesStatus.value is DevicesStatus.ReadyToRecord) {
            _recordingStatus.value = RecordingStatus.Ready
        } else {
            _recordingStatus.value = RecordingStatus.NotReady
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
        private const val INITIAL_TIME_OF_PREPARING = 3L
        private const val TIME_BETWEEN_CHRONOMETER_UPDATES = 10L
    }
}