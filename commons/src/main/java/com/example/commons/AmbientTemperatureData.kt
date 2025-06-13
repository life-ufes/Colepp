package com.example.commons

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class AmbientTemperatureData(
    val temperature: Float,
    val timestamp: Long
) {
    fun toByteArray(): ByteArray {
        return ByteBuffer.allocate(Float.SIZE_BYTES + Long.SIZE_BYTES)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putFloat(temperature)
            .putLong(timestamp)
            .array()
    }

    companion object {
        fun fromByteArray(data: ByteArray): AmbientTemperatureData {
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            return AmbientTemperatureData(
                temperature = buffer.float,
                timestamp = buffer.long
            )
        }
    }
}
