package com.example.transferdata.database.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.transferdata.database.model.HeartRatePolarEntity

@Dao
abstract class HeartRatePolarDao : BaseDao<HeartRatePolarEntity> {
    @Query("SELECT heartRate FROM ${HeartRatePolarEntity.TABLE_NAME} LIMIT 1000")
    abstract suspend fun getDataLimited(): List<Int>

    @Query("SELECT timestamp FROM ${HeartRatePolarEntity.TABLE_NAME} ORDER BY timestamp LIMIT 1000")
    abstract suspend fun getLimitedSampleOfTimestamp(): List<Long>
}