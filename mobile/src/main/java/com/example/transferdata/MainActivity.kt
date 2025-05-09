package com.example.transferdata

import BluetoothStateReceiver
import android.bluetooth.BluetoothAdapter
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.example.commons.Capabilities.Companion.ACCELEROMETER_CAPABILITY
import com.example.commons.CommunicationPaths.Companion.INIT_ACCELEROMETER_TRANSFER_DATA_PATH
import com.example.transferdata.WearableHandler.WearableViewModel
import com.example.transferdata.bluetoothHandler.BluetoothViewModel
import com.example.transferdata.polarHandler.PolarBleApiSingleton
import com.example.transferdata.presentation.recording.RecordingScreen
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.AvailabilityException
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import com.polar.sdk.api.PolarBleApiDefaultImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await
import java.time.Instant


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var api: PolarBleApiSingleton
    private lateinit var bluetoothStateReceiver: BluetoothStateReceiver
    private val bluetoothViewModel: BluetoothViewModel by viewModels()

    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }
    private val wearableViewModel by viewModels<WearableViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "version: " + PolarBleApiDefaultImpl.versionInfo())

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.background_color)
                ) {
                    var apiAvailable by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        apiAvailable = isAvailable(capabilityClient)
                    }

//                    MainNavHost(
//                        onBackPressed = { this@MainActivity.onBackPressedDispatcher.onBackPressed() },
//                        onClosePressed = { finish() },
//                        sendCount = {
//                            CoroutineScope(Dispatchers.IO).launch {
//                                sendCount(it)
//                            }
//                        },
//                        apiAvailable = false,
//                    )
                    RecordingScreen(
                        onBackPressed = {},
                        startRecording = { sendStartRecording() },
                        stopRecording = {},
                    )
                }
            }
        }

        bluetoothObserver()

        configurePolarApi()
    }

    private fun configurePolarApi() {
        api = PolarBleApiSingleton.getInstance(this)
    }

    private suspend fun isAvailable(api1: GoogleApi<*>): Boolean {
        return try {
            Log.d(TAG, "Checking API availability")
            GoogleApiAvailability.getInstance()
                .checkApiAvailability(api1)
                .await()
            Log.d(TAG, "API is available")
            true
        } catch (e: AvailabilityException) {
            Log.d(
                TAG,
                "${api1.javaClass.simpleName} API is not available in this device."
            )
            false
        }
    }

    private fun sendStartRecording() {
        wearableViewModel.capabilityInfos.value[ACCELEROMETER_CAPABILITY]?.let { nodes ->
            nodes.forEach { node ->
                messageClient.sendMessage(
                    node.id,
                    INIT_ACCELEROMETER_TRANSFER_DATA_PATH,
                    byteArrayOf()
                )
                    .addOnSuccessListener {
                        Log.d(TAG, "Initialized accelerometer transfer successfully")
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Failed to send message: $exception")
                    }
            }
        }

        // TODO remover isso daqui
        api.connect()

        api.initHrListener(
            onNext = { hrData ->
                hrData.samples.forEach { sample ->
                    Log.d(
                        TAG,
                        "Received HR data: ${sample.hr} at ${Instant.now()}"
                    )
                }
            },
            onError = { error ->
                Log.e(TAG, "Error: $error")
            },
            onComplete = {
                Log.d(TAG, "Completed")
            }
        )
    }

    private fun bluetoothObserver() {
//         Observar mudanças no estado do Bluetooth
        bluetoothViewModel.bluetoothEnable.observe(this) { isEnabled ->
            val message = if (isEnabled) {
                "Bluetooth ativado"
            } else {
                "Bluetooth desativado"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT)
                .show()
        }
        bluetoothStateReceiver = BluetoothStateReceiver(bluetoothViewModel)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(
            bluetoothStateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
        api.foregroundEntered()

        dataClient.addListener(wearableViewModel)
        messageClient.addListener(wearableViewModel)
        capabilityClient.addListener(
            wearableViewModel,
            Uri.parse("wear://"),
            CapabilityClient.FILTER_REACHABLE
        )
        capabilityClient.getCapability(
            "wear",
            CapabilityClient.FILTER_REACHABLE
        ).addOnSuccessListener { capabilityInfo ->
            wearableViewModel.updateCapabilityInfo(
                mapOf(
                    capabilityInfo.name to capabilityInfo.nodes
                )
            )
            Log.d(TAG, "Capability info: ${capabilityInfo.nodes} for ${capabilityInfo.name}")
        }.addOnFailureListener { exception ->
            Log.d(TAG, "Failed to get capability info: $exception")
        }
    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(wearableViewModel)
        messageClient.removeListener(wearableViewModel)
        capabilityClient.removeListener(wearableViewModel)
    }

    public override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothStateReceiver)
        api.shutDown()
    }
}
