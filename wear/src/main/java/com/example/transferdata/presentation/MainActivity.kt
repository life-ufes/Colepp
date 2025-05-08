package com.example.transferdata.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.commons.Capabilities.Companion.ACCELEROMETER_CAPABILITY
import com.example.transferdata.R
import com.example.transferdata.presentation.theme.TransferDataTheme
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity() {
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }

    private var accelerometerSensor: Sensor? = null
    private lateinit var sensorManager: SensorManager

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            WearApp(mainViewModel)
        }

        configureObservers()
    }

    private fun configureObservers() {
        mainViewModel.accelerometerState.observe(this) { isActive ->
            if (isActive) {
                registerAccelerometerListener()
            } else {
                unregisterAccelerometerListener()
            }
        }
    }

    private fun registerAccelerometerListener() {
        accelerometerSensor?.let {
            sensorManager.registerListener(
                mainViewModel,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private fun unregisterAccelerometerListener() {
        sensorManager.unregisterListener(mainViewModel, accelerometerSensor)
    }

    private fun checkSensorAvailability() {
        if (accelerometerSensor == null) {
            Log.d(TAG, "Accelerometer sensor not available")
        } else {
            capabilityClient.addLocalCapability(ACCELEROMETER_CAPABILITY)
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
                .addOnSuccessListener {
                    Log.d(TAG, "Local capability $ACCELEROMETER_CAPABILITY added successfully")
                    Log.d(TAG, "Accelerometer sensor available")
                }
        }
    }

    override fun onResume() {
        super.onResume()
        messageClient.addListener(mainViewModel)
        checkSensorAvailability()
    }

    override fun onPause() {
        super.onPause()
        messageClient.removeListener(mainViewModel)
        unregisterAccelerometerListener()

        capabilityClient.removeLocalCapability(ACCELEROMETER_CAPABILITY)
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
            .addOnSuccessListener {
                Log.d(TAG, "Local capability $ACCELEROMETER_CAPABILITY removed successfully")
            }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}