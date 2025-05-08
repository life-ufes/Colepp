package com.example.transferdata.polarHandler

import android.content.Context
import android.util.Log
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PolarBleApiSingleton private constructor(
    context: Context
) {
    private var hrDisposable: Disposable? = null
    private var autoConnectDisposable: Disposable? = null

    private var _deviceId = MutableStateFlow<String?>(null)
    val deviceId = _deviceId.asStateFlow()
    private var _deviceConnected = MutableStateFlow(false)
    val deviceConnected = _deviceConnected.asStateFlow()



    private val polarBleApi: PolarBleApi = PolarBleApiDefaultImpl.defaultImplementation(
        context.applicationContext,
        setOf(
            PolarBleApi.PolarBleSdkFeature.FEATURE_HR,
            PolarBleApi.PolarBleSdkFeature.FEATURE_BATTERY_INFO,
            PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_H10_EXERCISE_RECORDING,
            PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_OFFLINE_RECORDING,
            PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING,
            PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_DEVICE_TIME_SETUP,
            PolarBleApi.PolarBleSdkFeature.FEATURE_DEVICE_INFO
        )
    )

    companion object {
        private const val TAG = "PolarBleApiSingleton"
        private var instance: PolarBleApiSingleton? = null
        fun getInstance(
            context: Context
        ): PolarBleApiSingleton {

            return instance ?: synchronized(this) {
                instance ?: PolarBleApiSingleton(context).also {
                    instance = it
                }
            }
        }
    }

    fun connect() {
        polarBleApi.setApiCallback(myCallback)
        if (autoConnectDisposable != null) {
            autoConnectDisposable?.dispose()
        }
        autoConnectDisposable = polarBleApi.autoConnectToDevice(-60, "180D", null)
            .subscribe(
                { Log.d(TAG, "auto connect search complete") },
                { throwable: Throwable -> Log.e(TAG, "Error on auto connect", throwable) }
            )
    }

    fun initHrListener(
        onNext: (PolarHrData) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit,
    ) {
        val isDisposed = hrDisposable?.isDisposed ?: true
        if (!isDisposed) {
            hrDisposable?.dispose()
        }
        deviceId.value?.let {
            hrDisposable = polarBleApi.startHrStreaming(it)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError, onComplete)
        }
    }

    fun stopHrListener() {
        hrDisposable?.dispose()
    }

    fun disconnect() {
        deviceId.value?.let { polarBleApi.disconnectFromDevice(it) }
    }

    private val myCallback = object : PolarBleApiCallback() {
        override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
            Log.d(TAG, "CONNECTED: ${polarDeviceInfo.deviceId}")
            _deviceId.value = polarDeviceInfo.deviceId
            _deviceConnected.value = true
        }

        override fun deviceConnecting(polarDeviceInfo: PolarDeviceInfo) {
            Log.d(TAG, "CONNECTING: ${polarDeviceInfo.deviceId}")
        }

        override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
            Log.d(TAG, "DISCONNECTED: ${polarDeviceInfo.deviceId}")
            _deviceId.value = null
            _deviceConnected.value = false
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
}