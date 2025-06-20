package com.example.transferdata.database.model

class HeartRateGenericData(
    val heartRate: Int,
    timestamp: Long
): SensorGenericData(timestamp){
    override fun printValueInCsvFormat(): String {
        return "$heartRate"
    }
}