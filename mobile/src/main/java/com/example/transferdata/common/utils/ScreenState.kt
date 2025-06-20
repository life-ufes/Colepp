package com.example.transferdata.common.utils

sealed class ScreenState {
    data object Loading : ScreenState()
    data object Content : ScreenState()
    data class Error(val message: String) : ScreenState()
}