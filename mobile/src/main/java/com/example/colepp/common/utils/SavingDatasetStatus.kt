package com.example.colepp.common.utils

import android.net.Uri

sealed class SavingDatasetStatus {
    data object Saving : SavingDatasetStatus()
    data class Success(val file: Uri) : SavingDatasetStatus()
    data class Error(val message: String) : SavingDatasetStatus()
}