package com.example.transferdata.database.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.transferdata.database.model.GyroscopeSmartwatchEntity
import com.example.transferdata.database.model.ThreeAxisSensorValue

@Dao
abstract class GyroscopeSmartwatchDao: BaseDao<GyroscopeSmartwatchEntity> {
    @Query("SELECT x, y, z FROM ${GyroscopeSmartwatchEntity.TABLE_NAME} LIMIT 1000")
    abstract suspend fun getDataLimited(): List<ThreeAxisSensorValue>

    @Query("SELECT timestamp FROM ${GyroscopeSmartwatchEntity.TABLE_NAME} ORDER BY timestamp LIMIT 1000")
    abstract suspend fun getLimitedSampleOfTimestamp(): List<Long>
}