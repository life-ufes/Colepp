package com.example.transferdata.database.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.transferdata.database.model.HeartRateSmartwatchEntity

@Dao
abstract class HeartRateSmartwatchDao : BaseDao<HeartRateSmartwatchEntity> {

    @Query("SELECT timestamp FROM ${HeartRateSmartwatchEntity.TABLE_NAME} ORDER BY timestamp LIMIT 1000")
    abstract suspend fun getLimitedSampleOfTimestamp(): List<Long>
}