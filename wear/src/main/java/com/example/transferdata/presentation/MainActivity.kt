package com.example.transferdata.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureClient
import androidx.health.services.client.data.DataType
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.unregisterMeasureCallback
import androidx.lifecycle.lifecycleScope
import com.example.commons.Capabilities.Companion.ACCELEROMETER_CAPABILITY
import com.example.commons.Capabilities.Companion.AMBIENT_TEMPERATURE_CAPABILITY
import com.example.commons.Capabilities.Companion.GRAVITY_CAPABILITY
import com.example.commons.Capabilities.Companion.GYROSCOPE_CAPABILITY
import com.example.commons.Capabilities.Companion.HEART_RATE_CAPABILITY
import com.example.commons.Capabilities.Companion.LINEAR_ACCELERATION_CAPABILITY
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }
    private lateinit var measureClient: MeasureClient

    private var accelerometerSensor: Sensor? = null
    private var linearAccelerationSensor: Sensor? = null
    private var ambientTemperatureSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null
    private var gravitySensor: Sensor? = null
    private lateinit var sensorManager: SensorManager

    private val mainViewModel by viewModels<MainViewModel>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            lifecycleScope.launch {
                val capabilities = measureClient.getCapabilities()
                Log.d(TAG, "Capabilities: $capabilities")
                if (DataType.HEART_RATE_BPM in capabilities.supportedDataTypesMeasure){
                    capabilityClient.addLocalCapability(HEART_RATE_CAPABILITY)
                        .addOnFailureListener { exception ->
                            exception.printStackTrace()
                        }
                        .addOnSuccessListener {
                            Log.d(TAG, "Local capability $HEART_RATE_CAPABILITY added successfully")
                        }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        ambientTemperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        val healthServicesClient = HealthServices.getClient(applicationContext)
        measureClient = healthServicesClient.measureClient

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
        linearAccelerationSensor?.let {
            sensorManager.registerListener(mainViewModel, it, DELAY_FOR_ACCELEROMETER_25HZ)
        }
        ambientTemperatureSensor?.let {
            sensorManager.registerListener(mainViewModel, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscopeSensor?.let {
            sensorManager.registerListener(mainViewModel, it, DELAY_FOR_GYROSCOPE_50HZ)
        }
        gravitySensor?.let {
            sensorManager.registerListener(mainViewModel, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, mainViewModel)
    }

    private fun unregisterSensorListener() {
        sensorManager.unregisterListener(mainViewModel)
        lifecycleScope.launch {
            measureClient.unregisterMeasureCallback(DataType.HEART_RATE_BPM, mainViewModel)
        }
    }

    private fun checkSensorAvailability() {
        val sensorMap = mapOf(
            ACCELEROMETER_CAPABILITY to accelerometerSensor,
            LINEAR_ACCELERATION_CAPABILITY to linearAccelerationSensor,
            AMBIENT_TEMPERATURE_CAPABILITY to ambientTemperatureSensor,
            GYROSCOPE_CAPABILITY to gyroscopeSensor,
            GRAVITY_CAPABILITY to gravitySensor
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
                    }
            }
        }

        requestPermissionLauncher.launch(
            android.Manifest.permission.BODY_SENSORS
        )
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
            LINEAR_ACCELERATION_CAPABILITY,
            AMBIENT_TEMPERATURE_CAPABILITY,
            HEART_RATE_CAPABILITY,
            GYROSCOPE_CAPABILITY,
            GRAVITY_CAPABILITY
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