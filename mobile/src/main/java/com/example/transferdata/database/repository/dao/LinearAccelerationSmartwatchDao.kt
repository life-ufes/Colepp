package com.example.transferdata.database.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.transferdata.database.model.LinearAccelerationSmartwatchEntity
import com.example.transferdata.database.model.ThreeAxisSensorValue

@Dao
abstract class LinearAccelerationSmartwatchDao : BaseDao<LinearAccelerationSmartwatchEntity> {
    @Query(
        """
        SELECT x, y, z, timestamp
        FROM ${LinearAccelerationSmartwatchEntity.TABLE_NAME}
        WHERE recordId = :recordId
        ORDER BY timestamp ASC
        """
    )
    abstract suspend fun getAllDataFromRecord(recordId: Long): List<ThreeAxisSensorValue>
}