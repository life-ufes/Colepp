package com.example.transferdata.bluetoothHandler

import android.bluetooth.BluetoothAdapter
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class BluetoothStatus @Inject constructor() : BluetoothStateListener {
    private val _bluetoothEnable = MutableStateFlow(false)
    val bluetoothEnable = _bluetoothEnable.asStateFlow()

    fun checkBluetoothState(adapter: BluetoothAdapter) {
        _bluetoothEnable.value = adapter.isEnabled == true
    }

    override fun onBluetoothStateChanged(state: Int) {
        Log.d(TAG, "Bluetooth state changed: $state")
        when (state) {
            BluetoothAdapter.STATE_OFF -> {
                Log.d(TAG, "Bluetooth is OFF")
                _bluetoothEnable.value = false
            }

            BluetoothAdapter.STATE_ON -> {
                Log.d(TAG, "Bluetooth is ON")
                _bluetoothEnable.value = true
            }
        }
    }

    companion object {
        const val TAG = "BluetoothStatus"
    }
}