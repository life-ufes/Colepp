package com.example.transferdata.presentation

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.commons.AccelerometerData
import com.example.commons.CommunicationPaths.Companion.ACCELEROMETER_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.INIT_ACCELEROMETER_TRANSFER_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.STOP_ACCELEROMETER_TRANSFER_DATA_PATH
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application
) : AndroidViewModel(application),
    MessageClient.OnMessageReceivedListener,
    SensorEventListener {

    private val messageClient = Wearable.getMessageClient(application)
    private val nodeClient = Wearable.getNodeClient(application)

    private val _accelerometerState = MutableLiveData<Boolean>()
    val accelerometerState: LiveData<Boolean> = _accelerometerState

    private var currentNodeId: String? = null

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            INIT_ACCELEROMETER_TRANSFER_DATA_PATH -> {
                startAccelerometerTransfer(messageEvent.sourceNodeId)
            }

            STOP_ACCELEROMETER_TRANSFER_DATA_PATH -> {
                stopAccelerometerTransfer(messageEvent.sourceNodeId)
            }
        }
    }

    private fun startAccelerometerTransfer(nodeId: String) {
        currentNodeId = nodeId
        _accelerometerState.postValue(true)
    }

    private fun stopAccelerometerTransfer(nodeId: String) {
        if (nodeId == currentNodeId) {
            currentNodeId = null
            _accelerometerState.postValue(false)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val data = AccelerometerData(
                x = event.values[0],
                y = event.values[1],
                z = event.values[2],
                timestamp = event.timestamp
            )
            sendAccelerometerData(data)
        }
    }

    private fun sendAccelerometerData(data: AccelerometerData) {
        viewModelScope.launch(Dispatchers.IO) {
            val dataBytes = "${data.x},${data.y},${data.z},${data.timestamp}".toByteArray()
            nodeClient.connectedNodes
                .addOnSuccessListener { nodes ->
                    for (node in nodes) {
                        messageClient.sendMessage(
                            node.id,
                            ACCELEROMETER_DATA_PATH,
                            dataBytes
                        ).addOnSuccessListener {
                            // Handle success
                        }.addOnFailureListener {
                            // Handle failure
                        }
                    }
                }.addOnFailureListener {
                    // Handle failure to get connected nodes
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