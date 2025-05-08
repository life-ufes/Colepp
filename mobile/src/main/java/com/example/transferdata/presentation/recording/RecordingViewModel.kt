package com.example.transferdata.presentation.recording

import androidx.lifecycle.ViewModel
import com.example.transferdata.polarHandler.PolarBleApiSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor() : ViewModel() {
    private var polarApi : PolarBleApiSingleton? = null

    fun setPolarApi(polarApi: PolarBleApiSingleton){
        this.polarApi = polarApi
    }
}