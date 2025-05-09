package com.example.transferdata.WearableHandler

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WearableViewModel @Inject constructor() : ViewModel(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    private val _capabilityInfos = MutableStateFlow<Map<String, Set<Node>>>(emptyMap())
    val capabilityInfos = _capabilityInfos.asStateFlow()

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "Data changed: $dataEvents")
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "Message received: ${messageEvent.path} with ${messageEvent.data}")
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
        val updatedInfos = _capabilityInfos.value.toMutableMap()
        infos.forEach { (key, values) ->
            updatedInfos[key] = updatedInfos.getOrDefault(key, emptySet()) + values
        }
        _capabilityInfos.value = updatedInfos
    }

    companion object {
        private const val TAG = "WearableViewModel"
    }
}