package com.example.colepp.presentation.recording

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.colepp.common.utils.JsonHandler.getDecodedJsonParam
import com.example.colepp.navigation.NewRecordingNavGraphArgs.RECORDING_DESCRIPTION
import com.example.colepp.navigation.NewRecordingNavGraphArgs.RECORDING_TITLE
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val recordTitle = savedStateHandle.getDecodedJsonParam<String>(RECORDING_TITLE)
    val recordDescription =
        savedStateHandle.getDecodedJsonParam<String?>(RECORDING_DESCRIPTION).orEmpty()
}