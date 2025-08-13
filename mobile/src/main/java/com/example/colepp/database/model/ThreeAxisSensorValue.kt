package com.example.colepp.database.model

class ThreeAxisSensorValue(
    val x: Float,
    val y: Float,
    val z: Float,
    timestamp: Long
): SensorGenericData(timestamp) {
    override fun printValueInCsvFormat(): String {
        return "$x,$y,$z"
    }

    override fun toString(): String {
        return "$x,$y,$z"
    }
}
