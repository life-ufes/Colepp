package com.example.transferdata.common

sealed class WearableState {
    data object Waiting : WearableState()
    data object Transferring : WearableState()
}