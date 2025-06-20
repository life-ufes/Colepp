package com.example.transferdata.common.utils

import android.util.Log
import com.example.transferdata.database.model.SensorGenericData

class SampleSensorGenericData(
    private var data: List<SensorGenericData>,
) {
    val frequency = getHzFromNanoSec(data.take(1000).map { it.timestamp })
    private var lastData: Triple<Int, Pair<Long, Long>, SensorGenericData?>? = null

    fun getValueOfTimestamp(timestamp: Long): SensorGenericData? {
        if (lastData == null
            || (lastData!!.second.first > timestamp || lastData!!.second.second <= timestamp)) {
            data.withIndex().zipWithNext { current, next ->
                if (verificationIfBetween(timestamp, current.value.timestamp, next.value.timestamp)) {
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

        // Subtrai 1 para pegar o valor que estava antes de iniciar o intervalo
        val indexStart = data.indexOfFirst { it.timestamp >= startTime } - 1
        val indexFinish = data.indexOfLast { it.timestamp <= finishTime }

        if (indexStart < 0 || indexFinish < 0 || indexStart > indexFinish) {
            throw IllegalArgumentException("Invalid start or finish time for adjusting timestamps.")
        }

        data = data.subList(indexStart, indexFinish)
    }

    private fun getHzFromNanoSec(values: List<Long>): Double {
        if (values.size < 2) return 0.0
        val averageDiff = values
            .zipWithNext { a, b -> b - a }
            .average()
        return if (averageDiff > 0) 1_000_000_000.0 / averageDiff else 0.0
    }
}