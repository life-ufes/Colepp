package com.example.colepp.database.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.colepp.database.model.HeartRateSmartwatchEntity
import com.example.colepp.database.model.HeartRateGenericData

@Dao
abstract class HeartRateSmartwatchDao : BaseDao<HeartRateSmartwatchEntity> {
    @Query(
        """
        SELECT heartRate, timestamp
        FROM ${HeartRateSmartwatchEntity.TABLE_NAME}
        WHERE recordId = :recordId
        ORDER BY timestamp ASC
        """
    )
    abstract suspend fun getAllDataFromRecord(recordId: Long): List<HeartRateGenericData>
}