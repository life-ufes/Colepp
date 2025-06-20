package com.example.transferdata.database.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.transferdata.database.model.AmbientTemperatureGenericData
import com.example.transferdata.database.model.AmbientTemperatureSmartwatchEntity

@Dao
abstract class AmbientTemperatureSmartwatchDao : BaseDao<AmbientTemperatureSmartwatchEntity> {
    @Query(
        """
        SELECT temperature, timestamp
        FROM ${AmbientTemperatureSmartwatchEntity.TABLE_NAME}
        WHERE recordId = :recordId
        ORDER BY timestamp ASC
        """
    )
    abstract suspend fun getAllDataFromRecord(recordId: Long): List<AmbientTemperatureGenericData>
}