package com.example.transferdata.database.model

class AmbientTemperatureGenericData(
    val temperature: Float,
    timestamp: Long
) : SensorGenericData(timestamp){
    override fun printValueInCsvFormat(): String {
        return "$temperature"
    }
}