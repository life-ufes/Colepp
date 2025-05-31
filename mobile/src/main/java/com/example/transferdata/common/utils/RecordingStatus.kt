package com.example.transferdata.common.utils

sealed class RecordingStatus {
    data object NotReady : RecordingStatus()
    data object Ready : RecordingStatus()
    data object Preparing : RecordingStatus()
    data object Running : RecordingStatus()
    data object Finished : RecordingStatus()
    data object Error : RecordingStatus()
}