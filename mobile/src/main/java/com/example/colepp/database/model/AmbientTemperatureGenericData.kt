package com.example.colepp.database.model

class AmbientTemperatureGenericData(
    val temperature: Float,
    timestamp: Long
) : SensorGenericData(timestamp){
    override fun printValueInCsvFormat(): String {
        return "$temperature"
    }
}