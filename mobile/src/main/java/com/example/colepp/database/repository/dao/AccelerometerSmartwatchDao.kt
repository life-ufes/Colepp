package com.example.colepp.database.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.colepp.database.model.AccelerometerSmartwatchEntity
import com.example.colepp.database.model.ThreeAxisSensorValue

@Dao
abstract class AccelerometerSmartwatchDao : BaseDao<AccelerometerSmartwatchEntity> {
    @Query(
        """
        SELECT x, y, z, timestamp
        FROM ${AccelerometerSmartwatchEntity.TABLE_NAME}
        WHERE recordId = :recordId
        ORDER BY timestamp ASC
        """
    )
    abstract suspend fun getAllDataFromRecord(recordId: Long): List<ThreeAxisSensorValue>
}