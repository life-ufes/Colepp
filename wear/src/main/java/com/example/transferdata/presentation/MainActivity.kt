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
import com.example.commons.Capabilities.Companion.AMBIENT_TEMPERATURE_CAPABILITY
import com.example.commons.Capabilities.Companion.GYROSCOPE_CAPABILITY
import com.example.commons.Capabilities.Companion.HEART_RATE_CAPABILITY
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity() {
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }

    private var accelerometerSensor: Sensor? = null
    private var ambientTemperatureSensor: Sensor? = null
    private var heartRateSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null
    private lateinit var sensorManager: SensorManager

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        ambientTemperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setContent {
            WearApp(mainViewModel)
        }

        configureObservers()
    }

    private fun configureObservers() {
        mainViewModel.sensorsListenerState.observe(this) { isActive ->
            if (isActive) {
                registerSensorsListener()
            } else {
                unregisterSensorListener()
            }
        }
    }

    private fun registerSensorsListener() {
        accelerometerSensor?.let {
            sensorManager.registerListener(mainViewModel, it, DELAY_FOR_ACCELEROMETER_25HZ)
        }
        ambientTemperatureSensor?.let {
            sensorManager.registerListener(mainViewModel, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        heartRateSensor?.let {
            sensorManager.registerListener(mainViewModel, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscopeSensor?.let {
            sensorManager.registerListener(mainViewModel, it, DELAY_FOR_GYROSCOPE_50HZ)
        }
    }

    private fun unregisterSensorListener() {
        sensorManager.unregisterListener(mainViewModel)
    }

    private fun checkSensorAvailability() {
        val sensorMap = mapOf(
            ACCELEROMETER_CAPABILITY to accelerometerSensor,
            AMBIENT_TEMPERATURE_CAPABILITY to ambientTemperatureSensor,
            HEART_RATE_CAPABILITY to heartRateSensor,
            GYROSCOPE_CAPABILITY to gyroscopeSensor
        )

        sensorMap.forEach { (capability, sensor) ->
            if (sensor == null) {
                Log.d(TAG, "$capability sensor not available")
            } else {
                capabilityClient.addLocalCapability(capability)
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                    }
                    .addOnSuccessListener {
                        Log.d(TAG, "Local capability $capability added successfully")
                        Log.d(TAG, "Accelerometer sensor available")
                    }
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
        unregisterSensorListener()

        val capabilityList = listOf(
            ACCELEROMETER_CAPABILITY,
            AMBIENT_TEMPERATURE_CAPABILITY,
            HEART_RATE_CAPABILITY,
            GYROSCOPE_CAPABILITY
        )
        capabilityList.forEach { capability ->
            capabilityClient.removeLocalCapability(capability)
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
                .addOnSuccessListener {
                    Log.d(TAG, "Local capability $capability removed successfully")
                }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val DELAY_FOR_ACCELEROMETER_25HZ = 40_000
        private const val DELAY_FOR_GYROSCOPE_50HZ = 20_000
    }
}