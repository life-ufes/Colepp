package com.example.colepp.common

sealed class WearableState {
    data object Waiting : WearableState()
    data object Transferring : WearableState()
}