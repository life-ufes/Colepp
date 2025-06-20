package com.example.transferdata.dataset

import com.example.transferdata.common.utils.SampleSensorGenericData
import com.example.transferdata.common.utils.appendIfNotEmpty
import com.example.transferdata.database.repository.RecordDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException

class DatasetGenerator(
    private val recordDatabase: RecordDatabase
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    fun generateDataset(recordId: Long, file: File) {
        scope.launch {
            val allData = mutableMapOf<String, SampleSensorGenericData>()

            try {
                val record = recordDatabase.recordDao().getById(recordId)
                if (record == null) {
                    throw IllegalArgumentException("Record with id $recordId not found.")
                }
                val writer = FileWriter(file)
                val header = StringBuilder()

                recordDatabase.gyroscopeSmartwatchDao().getAllDataFromRecord(recordId)
                    .takeIf { it.isNotEmpty() }?.apply {
                        try {
                            allData[GYRO_WATCH_MAP_NAME] = SampleSensorGenericData(this).apply {
                                adjustTimestamps(
                                    record.clockSkewSmartwatchNanos,
                                    record.starRecordingNanos,
                                    record.stopRecordingNanos
                                )
                            }
                            header.appendIfNotEmpty(
                                "$GYRO_WATCH_COLUMN_X,$GYRO_WATCH_COLUMN_Y,$GYRO_WATCH_COLUMN_Z"
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                recordDatabase.accelerometerPolarDao().getAllDataFromRecord(recordId)
                    .takeIf { it.isNotEmpty() }?.apply {
                        try {
                            allData[ACC_POLAR_MAP_NAME] = SampleSensorGenericData(this).apply {
                                val clockSkew =
                                    record.starRecordingNanos + DELTA_1970_TO_2000_NANOS - (record.starRecordingMilli * 1_000_000L)
                                adjustTimestamps(
                                    clockSkew,
                                    record.starRecordingNanos,
                                    record.stopRecordingNanos
                                )
                            }
                            header.appendIfNotEmpty(
                                "$ACC_POLAR_COLUMN_X,$ACC_POLAR_COLUMN_Y,$ACC_POLAR_COLUMN_Z"
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                recordDatabase.accelerometerSmartwatchDao().getAllDataFromRecord(recordId)
                    .takeIf { it.isNotEmpty() }?.apply {
                        try{
                            allData[ACC_WATCH_MAP_NAME] = SampleSensorGenericData(this).apply {
                                adjustTimestamps(
                                    record.clockSkewSmartwatchNanos,
                                    record.starRecordingNanos,
                                    record.stopRecordingNanos
                                )
                            }
                            header.appendIfNotEmpty(
                                "$ACC_WATCH_COLUMN_X,$ACC_WATCH_COLUMN_Y,$ACC_WATCH_COLUMN_Z"
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                recordDatabase.heartRatePolarDao().getAllDataFromRecord(recordId)
                    .takeIf { it.isNotEmpty() }?.apply {
                        try{
                            allData[HR_POLAR_MAP_NAME] = SampleSensorGenericData(this).apply {
                                adjustTimestamps(
                                    0L,
                                    record.starRecordingNanos,
                                    record.stopRecordingNanos
                                )
                            }
                            header.appendIfNotEmpty(HR_POLAR_COLUMN)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                recordDatabase.heartRateSmartwatchDao().getAllDataFromRecord(recordId)
                    .takeIf { it.isNotEmpty() }?.apply {
                        try{
                            allData[HR_WATCH_MAP_NAME] = SampleSensorGenericData(this).apply {
                                adjustTimestamps(
                                    record.clockSkewSmartwatchNanos,
                                    record.starRecordingNanos,
                                    record.stopRecordingNanos
                                )
                            }
                            header.appendIfNotEmpty(HR_WATCH_COLUMN)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                recordDatabase.ambientTemperatureSmartwatchDao().getAllDataFromRecord(recordId)
                    .takeIf { it.isNotEmpty() }?.apply {
                        try{
                            allData[AMB_TEMP_WATCH_MAP_NAME] = SampleSensorGenericData(this).apply {
                                adjustTimestamps(
                                    record.clockSkewSmartwatchNanos,
                                    record.starRecordingNanos,
                                    record.stopRecordingNanos
                                )
                            }
                            header.appendIfNotEmpty(AMB_TEMP_WATCH_COLUMN)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                header.appendIfNotEmpty(TIMESTAMP_COLUMN)
                writer.appendLine(header.toString())

                val biggestFreq = allData.maxOfOrNull { it.value.frequency } ?: 0.0
                val referenceData =
                    allData.entries.firstOrNull { it.value.frequency == biggestFreq }
                // começa do indice 1 por causa que o adjustTimestamps adiciona um dado que
                // estava de antes de o coleeta começar
                for (i in 1 until (referenceData?.value?.getDataSize() ?: 0)) {
                    val currentTimestamp = referenceData?.value?.getValueOfIndex(i)?.timestamp
                        ?: break

                    val line = StringBuilder()
                    allData[GYRO_WATCH_MAP_NAME]?.getValueOfTimestamp(currentTimestamp)?.let {
                        line.appendIfNotEmpty(it.printValueInCsvFormat())
                    }
                    allData[ACC_POLAR_MAP_NAME]?.getValueOfTimestamp(currentTimestamp)?.let {
                        line.appendIfNotEmpty(it.printValueInCsvFormat())
                    }
                    allData[ACC_WATCH_MAP_NAME]?.getValueOfTimestamp(currentTimestamp)?.let {
                        line.appendIfNotEmpty(it.printValueInCsvFormat())
                    }
                    allData[HR_POLAR_MAP_NAME]?.getValueOfTimestamp(currentTimestamp)?.let {
                        line.appendIfNotEmpty(it.printValueInCsvFormat())
                    }
                    allData[HR_WATCH_MAP_NAME]?.getValueOfTimestamp(currentTimestamp)?.let {
                        line.appendIfNotEmpty(it.printValueInCsvFormat())
                    }
                    allData[AMB_TEMP_WATCH_MAP_NAME]?.getValueOfTimestamp(currentTimestamp)?.let {
                        line.appendIfNotEmpty(it.printValueInCsvFormat())
                    }

                    line.appendIfNotEmpty(currentTimestamp.toString())
                    writer.appendLine(line.toString())
                    if (i % 1000 == 0) {
                        writer.flush()
                    }
                }
                writer.flush()
                writer.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val ACC_POLAR_MAP_NAME = "acc_polar_map"
        private const val ACC_WATCH_MAP_NAME = "acc_watch_map"
        private const val GYRO_WATCH_MAP_NAME = "gyro_watch_map"
        private const val HR_POLAR_MAP_NAME = "hr_polar_map"
        private const val HR_WATCH_MAP_NAME = "hr_watch_map"
        private const val AMB_TEMP_WATCH_MAP_NAME = "amb_temp_watch_map"

        private const val GYRO_WATCH_COLUMN_X = "gyro_watch_x"
        private const val GYRO_WATCH_COLUMN_Y = "gyro_watch_y"
        private const val GYRO_WATCH_COLUMN_Z = "gyro_watch_z"
        private const val ACC_POLAR_COLUMN_X = "acc_polar_x"
        private const val ACC_POLAR_COLUMN_Y = "acc_polar_y"
        private const val ACC_POLAR_COLUMN_Z = "acc_polar_z"
        private const val ACC_WATCH_COLUMN_X = "acc_watch_x"
        private const val ACC_WATCH_COLUMN_Y = "acc_watch_y"
        private const val ACC_WATCH_COLUMN_Z = "acc_watch_z"
        private const val HR_POLAR_COLUMN = "hr_polar"
        private const val HR_WATCH_COLUMN = "hr_watch"
        private const val AMB_TEMP_WATCH_COLUMN = "amb_temp_watch"
        private const val TIMESTAMP_COLUMN = "timestamp_nanos"

        private const val DELTA_1970_TO_2000_NANOS = 946_684_800_000_000_000L
    }
}