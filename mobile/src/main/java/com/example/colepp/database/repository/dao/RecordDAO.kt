package com.example.colepp.database.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.colepp.database.model.RecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecordDAO : BaseDao<RecordEntity> {
    @Query("SELECT * FROM ${RecordEntity.TABLE_NAME} ORDER BY starRecordingMilli DESC")
    abstract fun getAll(): Flow<List<RecordEntity>>

    @Query("UPDATE ${RecordEntity.TABLE_NAME} SET clockSkewSmartwatchNanos = :clockSkew WHERE id = :id")
    abstract suspend fun setClockSkew(id: Long, clockSkew: Long): Int

    @Query("UPDATE ${RecordEntity.TABLE_NAME} SET starRecordingNanos = :starRecordingNanos, starRecordingMilli = :starRecordingMilli WHERE id = :id")
    abstract suspend fun setStarRecordingTime(id: Long, starRecordingNanos: Long, starRecordingMilli: Long): Int

    @Query("UPDATE ${RecordEntity.TABLE_NAME} SET stopRecordingNanos = :stopRecordingNanos, stopRecordingMilli = :stopRecordingMilli WHERE id = :id")
    abstract suspend fun setStopRecordingTime(id: Long, stopRecordingNanos: Long, stopRecordingMilli: Long): Int

    @Query("SELECT * FROM ${RecordEntity.TABLE_NAME} WHERE id = :id")
    abstract suspend fun getById(id: Long): RecordEntity?

    @Query("DELETE FROM ${RecordEntity.TABLE_NAME} WHERE id = :id")
    abstract suspend fun deleteById(id: Long): Int

    @Query("UPDATE ${RecordEntity.TABLE_NAME} SET title = :title, description = :description WHERE id = :id")
    abstract suspend fun updateRecord(id: Long, title: String, description: String): Int

    @Query("UPDATE ${RecordEntity.TABLE_NAME} SET shared = 1 WHERE id = :id")
    abstract suspend fun setShared(id: Long): Int
}