package com.example.commons

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class AccelerometerData(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
){
    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(3 * Float.SIZE_BYTES + Long.SIZE_BYTES)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.putFloat(x)
        buffer.putFloat(y)
        buffer.putFloat(z)
        buffer.putLong(timestamp)
        return buffer.array()
    }

    companion object {
        fun fromByteArray(data: ByteArray): AccelerometerData {
            val buffer = ByteBuffer.wrap(data)
            buffer.order(ByteOrder.LITTLE_ENDIAN)
            val x = buffer.float
            val y = buffer.float
            val z = buffer.float
            val timestamp = buffer.long
            return AccelerometerData(x, y, z, timestamp)
        }
    }
}