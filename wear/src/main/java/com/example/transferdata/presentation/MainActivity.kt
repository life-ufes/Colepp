package com.example.transferdata.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.commons.Capabilities.Companion.ACCELEROMETER_CAPABILITY
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity() {
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }

    private var accelerometerSensor: Sensor? = null
    private lateinit var sensorManager: SensorManager

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
                DELAY_FOR_ACCELEROMETER_25HZ
            )
        }
    }

    private fun unregisterAccelerometerListener() {
        sensorManager.unregisterListener(mainViewModel)
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
        private const val DELAY_FOR_ACCELEROMETER_25HZ = 40_000 // 25Hz
    }
}