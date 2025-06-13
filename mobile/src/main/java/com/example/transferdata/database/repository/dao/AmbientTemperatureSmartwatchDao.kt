package com.example.transferdata.database.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.transferdata.database.model.AmbientTemperatureSmartwatchEntity

@Dao
abstract class AmbientTemperatureSmartwatchDao : BaseDao<AmbientTemperatureSmartwatchEntity> {

    @Query("SELECT timestamp FROM ${AmbientTemperatureSmartwatchEntity.TABLE_NAME} ORDER BY timestamp LIMIT 1000")
    abstract suspend fun getLimitedSampleOfTimestamp(): List<Long>
}