package com.example.transferdata.database.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.transferdata.database.model.AccelerometerSmartwatchEntity
import com.example.transferdata.database.model.ThreeAxisSensorValue

@Dao
abstract class AccelerometerSmartwatchDao : BaseDao<AccelerometerSmartwatchEntity> {
    @Query("SELECT x, y, z FROM ${AccelerometerSmartwatchEntity.TABLE_NAME} LIMIT 1000")
    abstract suspend fun getDataLimited(): List<ThreeAxisSensorValue>

    @Query("SELECT timestamp FROM ${AccelerometerSmartwatchEntity.TABLE_NAME} ORDER BY timestamp LIMIT 1000")
    abstract suspend fun getLimitedSampleOfTimestamp(): List<Long>
}