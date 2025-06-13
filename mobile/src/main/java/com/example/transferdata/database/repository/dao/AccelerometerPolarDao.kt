package com.example.transferdata.database.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.transferdata.database.model.AccelerometerPolarEntity
import com.example.transferdata.database.model.ThreeAxisSensorValue

@Dao
abstract class AccelerometerPolarDao : BaseDao<AccelerometerPolarEntity> {
    @Query("SELECT x, y, z FROM ${AccelerometerPolarEntity.TABLE_NAME} LIMIT 1000")
    abstract suspend fun getDataLimited(): List<ThreeAxisSensorValue>

    @Query("SELECT timestamp FROM ${AccelerometerPolarEntity.TABLE_NAME} ORDER BY timestamp LIMIT 1000")
    abstract suspend fun getLimitedSampleOfTimestamp(): List<Long>
}