package com.example.commons

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class HeartRateData(
    val heartRate: Int,
    val timestamp: Long
) {
    fun toByteArray(): ByteArray {
        return ByteBuffer.allocate(Int.SIZE_BYTES + 2 * Long.SIZE_BYTES)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(heartRate)
            .putLong(timestamp)
            .array()
    }

    companion object {
        fun fromByteArray(data: ByteArray): HeartRateData {
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            return HeartRateData(
                heartRate = buffer.int,
                timestamp = buffer.long
            )
        }
    }
}
