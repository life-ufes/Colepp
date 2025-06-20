package com.example.transferdata.database.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.transferdata.database.model.HeartRatePolarEntity
import com.example.transferdata.database.model.HeartRateGenericData

@Dao
abstract class HeartRatePolarDao : BaseDao<HeartRatePolarEntity> {
    @Query(
        """
        SELECT heartRate, timestamp
        FROM ${HeartRatePolarEntity.TABLE_NAME}
        WHERE recordId = :recordId
        ORDER BY timestamp ASC
        """
    )
    abstract suspend fun getAllDataFromRecord(recordId: Long): List<HeartRateGenericData>
}