package com.example.transferdata.presentation

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.commons.AccelerometerData
import com.example.commons.AmbientTemperatureData
import com.example.commons.CommunicationPaths.Companion.ACCELEROMETER_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.AMBIENT_TEMPERATURE_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.GYROSCOPE_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.HEART_RATE_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.INIT_TRANSFER_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.PING_PATH
import com.example.commons.CommunicationPaths.Companion.PONG_PATH
import com.example.commons.CommunicationPaths.Companion.STOP_TRANSFER_DATA_PATH
import com.example.commons.GyroscopeData
import com.example.commons.HeartRateData
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainViewModel(
    application: Application
) : AndroidViewModel(application),
    MessageClient.OnMessageReceivedListener,
    SensorEventListener {

    private val messageClient = Wearable.getMessageClient(application)
    private val nodeClient = Wearable.getNodeClient(application)

    private val _sensorsListenerState = MutableLiveData<Boolean>()
    val sensorsListenerState: LiveData<Boolean> = _sensorsListenerState

    private var currentNodeId: String? = null

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            PING_PATH -> {
            val t2 = SystemClock.elapsedRealtimeNanos()
            val data = ByteBuffer.allocate(Long.SIZE_BYTES)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putLong(t2)
                .array()

            sendPongMessage(messageEvent.sourceNodeId, messageEvent.data + data)
        }

            INIT_TRANSFER_DATA_PATH -> {
                startSensorsListener(messageEvent.sourceNodeId)
            }

            STOP_TRANSFER_DATA_PATH -> {
                stopSensorListeners(messageEvent.sourceNodeId)
            }
        }
    }

    private fun sendPongMessage(sourceNodeId: String, bytes: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("DataLayerListenerService", "Sending PONG message to node: $sourceNodeId")
            messageClient.sendMessage(
                sourceNodeId,
                PONG_PATH,
                bytes
            )
        }
    }

    private fun startSensorsListener(nodeId: String) {
        currentNodeId = nodeId
        _sensorsListenerState.postValue(true)
    }

    private fun stopSensorListeners(nodeId: String) {
        if (nodeId == currentNodeId) {
            currentNodeId = null
            _sensorsListenerState.postValue(false)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val data = AccelerometerData(
                    x = event.values[0],
                    y = event.values[1],
                    z = event.values[2],
                    timestamp = event.timestamp,
                )
                sendAccelerometerData(data)
            }

            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                val data = GyroscopeData(
                    x = event.values[0],
                    y = event.values[1],
                    z = event.values[2],
                    timestamp = event.timestamp,
                )
                sendGyroscopeData(data)
            }

            Sensor.TYPE_HEART_RATE -> {
                val data = HeartRateData(
                    heartRate = event.values[0].toInt(),
                    timestamp = event.timestamp,
                )
                sendHeartRateData(data)
            }

            Sensor.TYPE_GYROSCOPE -> {
                val data = AmbientTemperatureData(
                    temperature = event.values[0],
                    timestamp = event.timestamp,
                )
                sendAmbientTemperatureData(data)
            }
        }
    }

    private fun sendAccelerometerData(data: AccelerometerData) {
        viewModelScope.launch(Dispatchers.IO) {
            val dataBytes = data.toByteArray()
            nodeClient.connectedNodes
                .addOnSuccessListener { nodes ->
                    for (node in nodes) {
                        messageClient.sendMessage(
                            node.id,
                            ACCELEROMETER_DATA_PATH,
                            dataBytes
                        )
                    }
                }.addOnFailureListener {
                    // Handle failure to get connected nodes
                }
        }
    }

    private fun sendAmbientTemperatureData(data: AmbientTemperatureData) {
        viewModelScope.launch(Dispatchers.IO) {
            val dataBytes = data.toByteArray()
            currentNodeId?.let { nodeId ->
                messageClient.sendMessage(
                    nodeId,
                    AMBIENT_TEMPERATURE_DATA_PATH,
                    dataBytes
                )
            }
        }
    }

    private fun sendHeartRateData(data: HeartRateData) {
        viewModelScope.launch(Dispatchers.IO) {
            val dataBytes = data.toByteArray()
            currentNodeId?.let { nodeId ->
                messageClient.sendMessage(
                    nodeId,
                    HEART_RATE_DATA_PATH,
                    dataBytes
                )
            }
        }
    }

    private fun sendGyroscopeData(data: GyroscopeData) {
        viewModelScope.launch(Dispatchers.IO) {
            val dataBytes = data.toByteArray()
            currentNodeId?.let { nodeId ->
                messageClient.sendMessage(
                    nodeId,
                    GYROSCOPE_DATA_PATH,
                    dataBytes
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No action needed
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY]!!
                MainViewModel(
                    application
                )
            }
        }
    }
}