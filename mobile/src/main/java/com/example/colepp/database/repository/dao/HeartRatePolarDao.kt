package com.example.colepp.database.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.colepp.database.model.HeartRatePolarEntity
import com.example.colepp.database.model.HeartRateGenericData

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