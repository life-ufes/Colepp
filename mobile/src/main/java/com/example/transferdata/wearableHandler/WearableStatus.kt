package com.example.transferdata.wearableHandler

import android.os.SystemClock
import android.util.Log
import com.example.commons.AmbientTemperatureData
import com.example.commons.Capabilities
import com.example.commons.Capabilities.Companion.WEAR_CAPABILITY
import com.example.commons.CommunicationPaths.Companion.ACCELEROMETER_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.AMBIENT_TEMPERATURE_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.GRAVITY_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.GYROSCOPE_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.HEART_RATE_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.INIT_TRANSFER_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.LINEAR_ACCELERATION_DATA_PATH
import com.example.commons.CommunicationPaths.Companion.PING_PATH
import com.example.commons.CommunicationPaths.Companion.PONG_PATH
import com.example.commons.CommunicationPaths.Companion.START_ACTIVITY_PATH
import com.example.commons.CommunicationPaths.Companion.STOP_TRANSFER_DATA_PATH
import com.example.commons.HeartRateData
import com.example.commons.ThreeAxisData
import com.example.transferdata.common.utils.PongResponse
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class WearableStatus @Inject constructor(
    private val messageClient: MessageClient
) : MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    private val _capabilityInfos = MutableStateFlow<Map<String, Set<Node>>>(emptyMap())
    val capabilityInfos = _capabilityInfos.asStateFlow()

    val wearableNode = _capabilityInfos.map {
        it[WEAR_CAPABILITY]?.firstOrNull()
    }.stateIn(
        CoroutineScope(Dispatchers.Default),
        started = SharingStarted.Lazily,
        initialValue = null
    )

    private val _accValue = MutableStateFlow<ThreeAxisData?>(null)
    val accValue = _accValue.asStateFlow()

    private val _linearAccValue = MutableStateFlow<ThreeAxisData?>(null)
    val linearAccValue = _linearAccValue.asStateFlow()

    private val _hrValue = MutableStateFlow<HeartRateData?>(null)
    val hrValue = _hrValue.asStateFlow()

    private val _ambientTemperatureValue =
        MutableStateFlow<AmbientTemperatureData?>(null)
    val ambientTemperatureValue = _ambientTemperatureValue.asStateFlow()

    private val _gyroscopeValue = MutableStateFlow<ThreeAxisData?>(null)
    val gyroscopeValue = _gyroscopeValue.asStateFlow()

    private val _gravityValue = MutableStateFlow<ThreeAxisData?>(null)
    val gravityValue = _gravityValue.asStateFlow()

    private val _syncTimeValue = MutableStateFlow<Long?>(null)
    val syncTimeValue = _syncTimeValue.asStateFlow()

    private suspend fun syncTime(nodeId: String) = coroutineScope {
        val pongChannel = Channel<PongResponse>(capacity = Channel.UNLIMITED)
        val offsets = mutableListOf<Long>()

        val listener = MessageClient.OnMessageReceivedListener { event ->
            if (event.path == PONG_PATH) {
                val t3 = SystemClock.elapsedRealtimeNanos()
                Log.d("TimeSync", "Received pong message: ${event.data.size} bytes")
                val buffer = ByteBuffer.wrap(event.data).order(ByteOrder.LITTLE_ENDIAN)
                val t1 = buffer.long
                val t2 = buffer.long
                Log.d(
                    "TimeSync",
                    "Pong data: t1=$t1, t2=$t2, t3=$t3, current time=${SystemClock.elapsedRealtimeNanos()}"
                )
                pongChannel.trySend(PongResponse(t1, t2, t3))
            }
        }

        messageClient.addListener(listener)

        try {
            repeat(HANDSHAKE_COUNT) { i ->
                val t1 = SystemClock.elapsedRealtimeNanos()
                Log.d("TimeSync", "Sending handshake #$i at t1=$t1")

                val payload = ByteBuffer.allocate(Long.SIZE_BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putLong(t1)
                    .array()
                messageClient.sendMessage(nodeId, PING_PATH, payload).await()

                delay(200)

                withTimeout(5000) {
                    val pong = pongChannel.receive()

                    val offset = pong.t1 - pong.t2 - ((pong.t3 - pong.t1) / 2)
                    offsets.add(offset)
                    Log.d("TimeSync", "Handshake #$i offset = $offset")

                }
            }
        } catch (e: Exception) {
            Log.e("TimeSync", "Error during sync", e)
        } finally {
            _syncTimeValue.value = offsets.takeIf { it.isNotEmpty() }?.average()?.toLong()?.also {
                Log.d("TimeSync", "Average offset = $it ns")
            }
            messageClient.removeListener(listener)
            pongChannel.close()
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            ACCELEROMETER_DATA_PATH -> {
                val data = messageEvent.data
                val accelerometerData = ThreeAxisData.fromByteArray(data)
                _accValue.value = accelerometerData
            }

            LINEAR_ACCELERATION_DATA_PATH -> {
                val data = messageEvent.data
                val linearAccelerationData = ThreeAxisData.fromByteArray(data)
                _linearAccValue.value = linearAccelerationData
            }

            HEART_RATE_DATA_PATH -> {
                val data = messageEvent.data
                val heartRateData = HeartRateData.fromByteArray(data)
                _hrValue.value = heartRateData
            }

            GYROSCOPE_DATA_PATH -> {
                val data = messageEvent.data
                val gyroscopeData = ThreeAxisData.fromByteArray(data)
                _gyroscopeValue.value = gyroscopeData
            }

            GRAVITY_DATA_PATH -> {
                val data = messageEvent.data
                val gravityData = ThreeAxisData.fromByteArray(data)
                _gravityValue.value = gravityData
            }

            AMBIENT_TEMPERATURE_DATA_PATH -> {
                val data = messageEvent.data
                val ambientTemperatureData = AmbientTemperatureData.fromByteArray(data)
                _ambientTemperatureValue.value = ambientTemperatureData
            }
        }
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        try {
            updateCapabilityInfo(
                mapOf(
                    capabilityInfo.name to capabilityInfo.nodes
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d(TAG, "Capability values: ${_capabilityInfos.value}")
    }

    fun updateCapabilityInfo(infos: Map<String, Set<Node>>) {
        Log.d(TAG, "Updating capability info: $infos")
        Log.d(TAG, "Current capability infos: ${_capabilityInfos.value}")
        val updatedInfos = _capabilityInfos.value.toMutableMap()
        infos.forEach { (key, values) ->
            updatedInfos[key] = values
        }
        _capabilityInfos.value = updatedInfos
    }

    fun sendStarWearAppMessage() {
        capabilityInfos.value[WEAR_CAPABILITY]?.let { nodes ->
            nodes.forEach { node ->
                messageClient.sendMessage(
                    node.id,
                    START_ACTIVITY_PATH,
                    null
                )
            }
        }
    }

    fun startTransferData() {
        Capabilities.getNodeCapabilities(capabilityInfos.value)
            .let { nodes ->
                nodes.forEach { node ->
                    sendStartTransferDataMessage(node.id)
                    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                        syncTime(node.id)
                    }
                }
            }
    }

    private fun sendStartTransferDataMessage(nodeId: String) {
        messageClient.sendMessage(
            nodeId,
            INIT_TRANSFER_DATA_PATH,
            null
        )
    }

    fun stopTransferData() {
        Capabilities.getNodeCapabilities(capabilityInfos.value)
            .let { nodes ->
                nodes.forEach { node ->
                    sendStopTransferDataMessage(node.id)
                }
            }
        _syncTimeValue.value = null
    }

    private fun sendStopTransferDataMessage(nodeId: String) {
        messageClient.sendMessage(
            nodeId,
            STOP_TRANSFER_DATA_PATH,
            null
        )
    }

    companion object {
        const val TAG = "WearableStatus"
        private const val HANDSHAKE_COUNT = 5
    }
}