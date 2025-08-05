package com.example.transferdata.common.utils

sealed class InstructionCard {
    data object InitNewRecord : InstructionCard()
    data object ConnectToDevice : InstructionCard()
    data object DeleteRecord : InstructionCard()
    data object EditRecord : InstructionCard()
    data object DownloadRecord : InstructionCard()
    data object ShareRecord : InstructionCard()
}