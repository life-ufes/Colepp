package com.example.transferdata.common.utils

import android.util.Log
import com.example.commons.Capabilities.Companion.ACCELEROMETER_CAPABILITY
import com.example.commons.Capabilities.Companion.WEAR_CAPABILITY
import com.google.android.gms.wearable.Node

sealed class DevicesStatus(val code: Int) {
    data object BluetoothOff : DevicesStatus(BLUETOOTH_OFF_CODE)
    data object OnlyBluetoothOn : DevicesStatus(BLUETOOTH_ON_CODE)
    data object OnlyPolarOn : DevicesStatus(POLAR_ON_CODE)
    data object OnlyWearOn : DevicesStatus(WEAR_ON_CODE)
    data object WearAndPolarOn : DevicesStatus(WEAR_AND_POLAR_ON_CODE)
    data object WearOnWithTransferCapability : DevicesStatus(WEAR_WITH_TRANSFER_CAPABILITY_CODE)
    data object ReadyToRecord : DevicesStatus(READY_TO_RECORD_CODE)

    companion object {
        const val BLUETOOTH_OFF_CODE = 0b0000
        const val BLUETOOTH_ON_CODE = 0b0001
        const val POLAR_ON_CODE = 0b0011
        const val WEAR_ON_CODE = 0b0101
        const val WEAR_AND_POLAR_ON_CODE = 0b0111
        const val WEAR_WITH_TRANSFER_CAPABILITY_CODE = 0b1101
        const val READY_TO_RECORD_CODE = 0b1111

        private val mapStates = mapOf(
//            BLUETOOTH_OFF_CODE to BluetoothOff,
            BLUETOOTH_ON_CODE to OnlyBluetoothOn,
            POLAR_ON_CODE to OnlyPolarOn,
            WEAR_ON_CODE to OnlyWearOn,
            WEAR_AND_POLAR_ON_CODE to WearAndPolarOn,
            WEAR_WITH_TRANSFER_CAPABILITY_CODE to WearOnWithTransferCapability,
            READY_TO_RECORD_CODE to ReadyToRecord
        )

        const val BLUETOOTH_BIT = 0b0001
        const val POLAR_BIT = 0b0010
        const val WEAR_BIT = 0b0100
        const val WEAR_WITH_TRANSFER_CAPABILITY_BIT = 0b1100

        fun getRecordingStatus(
            bluetoothState: Boolean,
            polarState: Boolean,
            wearCapabilities: Map<String, Set<Node>>
        ): DevicesStatus {
            Log.d("TAG", "BLUETOOTH_OFF_CODE = $BLUETOOTH_OFF_CODE map: $mapStates")
            Log.d("TAG", "getRecordingStatus: bluetoothState: $bluetoothState, polarState: $polarState, wearCapabilities: $wearCapabilities")
            var state = 0b0
//            state = state or BLUETOOTH_BIT

            if (bluetoothState) {
                state = state or BLUETOOTH_BIT
            }
            Log.d("TAG", "getRecordingStatus: bluetoothState: $bluetoothState, state: ${state.toString(2)}")
            if (polarState) state = state or POLAR_BIT
            Log.d("TAG", "getRecordingStatus: polarState: $polarState, state: ${state.toString(2)}")
            val wearCapability = wearCapabilities.getOrDefault(WEAR_CAPABILITY, emptySet())
            val transferCapability = wearCapabilities.getOrDefault(ACCELEROMETER_CAPABILITY, emptySet())

            if((wearCapability intersect transferCapability).isNotEmpty()) {
                state = state or WEAR_WITH_TRANSFER_CAPABILITY_BIT
            }
            Log.d("TAG", "getRecordingStatus: wearCapability: $wearCapability, state: ${state.toString(2)}")
            if (wearCapability.isNotEmpty()) {
                state = state or WEAR_BIT
            }
            Log.d("TAG", "getRecordingStatus: wearCapability: $wearCapability, state: ${state.toString(2)}")
            Log.d("TAG", "Binary code generated: ${state.toString(2)}")
            val value = mapStates.getOrDefault(state, BluetoothOff)
            Log.d("TAG", "getRecordingStatus: $value")
            return value
        }
    }
}