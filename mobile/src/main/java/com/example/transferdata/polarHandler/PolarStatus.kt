package com.example.transferdata.polarHandler

import android.os.SystemClock
import android.util.Log
import com.polar.androidcommunications.api.ble.model.DisInfo
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.model.PolarAccelerometerData
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHealthThermometerData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

class PolarStatus @Inject constructor(
    private val polarBleApi: PolarBleApi
) {
    private var hrDisposable: Disposable? = null
    private var accDisposable: Disposable? = null
    private var autoConnectDisposable: Disposable? = null

    private var _device = MutableStateFlow<PolarDeviceInfo?>(null)
    val device = _device.asStateFlow()

    private val _hrValue = MutableStateFlow<Pair<Int, Long>?>(null)
    val hrValue = _hrValue.asStateFlow()

    private val _accValue =
        MutableStateFlow<PolarAccelerometerData.PolarAccelerometerDataSample?>(null)
    val accValue = _accValue.asStateFlow()

    fun connect() {
        if (device.value != null) {
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

    fun startListeners() {
        initHrListener()
        initAccListener()
    }

    private fun initAccListener() {
        val isDisposed = accDisposable?.isDisposed ?: true
        if (!isDisposed) {
            accDisposable?.dispose()
        }
        device.value?.let {
            accDisposable =
                polarBleApi.requestStreamSettings(it.deviceId, PolarBleApi.PolarDeviceDataType.ACC)
                    .observeOn(AndroidSchedulers.mainThread())
                    .toFlowable()
                    .flatMap { settings ->

                        polarBleApi.startAccStreaming(it.deviceId, settings)
                    }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { accData ->
                            accData.samples.forEach { sample ->
                                _accValue.value = sample
                            }
                        },
                        { error ->
                            Log.e(TAG, "Error receiving accelerometer data", error)
                        }
                    )
        }
    }

    private fun initHrListener() {
        val isDisposed = hrDisposable?.isDisposed ?: true
        if (!isDisposed) {
            hrDisposable?.dispose()
        }
        device.value?.let {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            hrDisposable = polarBleApi.setLocalTime(it.deviceId, cal)
                .andThen(polarBleApi.startHrStreaming(it.deviceId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { hrData ->
                        hrData.samples.forEach { sample ->
                            val time = SystemClock.elapsedRealtimeNanos()
                            _hrValue.value = Pair(sample.hr, time)
                        }
                    },
                    { error ->
                        Log.e(TAG, "Error receiving HR data", error)
                    }
                )
        }
    }

    fun stopListeners() {
        _hrValue.value = null
        hrDisposable?.dispose()
        _accValue.value = null
        accDisposable?.dispose()
    }

    fun disconnect() {
        device.value?.let { polarBleApi.disconnectFromDevice(it.deviceId) }
    }

    private val myCallback = object : PolarBleApiCallback() {
        override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
            Log.d(TAG, "CONNECTED: ${polarDeviceInfo.deviceId}")
            _device.value = polarDeviceInfo
        }

        override fun deviceConnecting(polarDeviceInfo: PolarDeviceInfo) {
            Log.d(TAG, "CONNECTING: ${polarDeviceInfo.deviceId}")
        }

        override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
            Log.d(TAG, "DISCONNECTED: ${polarDeviceInfo.deviceId}")
            _device.value = null
        }

        override fun disInformationReceived(identifier: String, disInfo: DisInfo) {
            Log.d(TAG, "DIS INFO: $identifier - $disInfo")
        }

        override fun htsNotificationReceived(identifier: String, data: PolarHealthThermometerData) {
            Log.d(
                TAG,
                "HTS NOTIFICATION: $identifier - celsius: ${data.celsius}, fahrenheit: ${data.fahrenheit}"
            )
        }

        override fun batteryLevelReceived(identifier: String, level: Int) {
            Log.d(TAG, "BATTERY LEVEL: $level")
        }
    }

    fun foregroundEntered() {
        polarBleApi.foregroundEntered()
    }

    fun shutDown() {
        polarBleApi.shutDown()
    }

    companion object {
        const val TAG = "PolarStatus"
    }
}