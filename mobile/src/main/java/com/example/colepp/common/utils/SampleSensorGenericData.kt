package com.example.colepp.common.utils

import com.example.colepp.database.model.SensorGenericData

class SampleSensorGenericData(
    private var data: List<SensorGenericData>,
) {
    val frequency = getHzFromNanoSec(data.take(1000).map { it.timestamp })
    private var lastData: Triple<Int, Pair<Long, Long>, SensorGenericData?>? = null

    fun getValueOfTimestamp(timestamp: Long): SensorGenericData? {
        if (lastData == null
            || (lastData!!.second.first > timestamp || lastData!!.second.second <= timestamp)
        ) {
            data.withIndex().zipWithNext { current, next ->
                if (verificationIfBetween(
                        timestamp,
                        current.value.timestamp,
                        next.value.timestamp
                    )
                ) {
                    lastData = Triple(
                        current.index,
                        Pair(current.value.timestamp, next.value.timestamp),
                        current.value
                    )
                }
            }
        }
        return lastData?.third
    }

    private fun verificationIfBetween(timestamp: Long, first: Long, second: Long): Boolean {
        return timestamp in first..<second
    }

    fun getValueOfIndex(index: Int) = data.getOrNull(index)

    fun getDataSize() = data.size

    fun adjustTimestamps(clockSkew: Long, startTime: Long, finishTime: Long) {
        if (clockSkew != 0L) {
            data.forEach { it.timestamp += clockSkew }
        }

        // Subtract 1 to get the value before the start of the range if it is not the first value
        val indexStart = data.indexOfFirst { it.timestamp >= startTime }
            .let { if (it != 0) it - 1 else it }
        val indexFinish = data.indexOfLast { it.timestamp <= finishTime }

        if (indexStart < 0 || indexFinish < 0 || indexStart > indexFinish) {
            throw IllegalArgumentException("Invalid start or finish time for adjusting timestamps.")
        }

        data = data.subList(indexStart, indexFinish)
        data.firstOrNull()?.let {
            lastData = Triple(
                0,
                Pair(Long.MIN_VALUE, it.timestamp),
                it
            )
        }
    }

    private fun getHzFromNanoSec(values: List<Long>): Double {
        if (values.size < 2) return 0.0
        val averageDiff = values
            .zipWithNext { a, b -> b - a }
            .average()
        return if (averageDiff > 0) 1_000_000_000.0 / averageDiff else 0.0
    }
}