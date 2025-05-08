package com.example.transferdata.bluetoothHandler

import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor() : ViewModel(), BluetoothStateListener {
    private val _bluetoothEnable = MutableLiveData<Boolean>()
    val bluetoothEnable: LiveData<Boolean> = _bluetoothEnable

    init {
        checkBluetoothState()
    }

    private fun checkBluetoothState() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        _bluetoothEnable.postValue(bluetoothAdapter?.isEnabled == true)
    }

    override fun onBluetoothStateChanged(state: Int) {
        when (state) {
            BluetoothAdapter.STATE_OFF -> _bluetoothEnable.postValue(false)
            BluetoothAdapter.STATE_ON -> _bluetoothEnable.postValue(true)
        }
    }
}