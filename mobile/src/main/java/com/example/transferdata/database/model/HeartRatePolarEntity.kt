package com.example.transferdata.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = HeartRatePolarEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = RecordEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("recordId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["recordId"])]
)
data class HeartRatePolarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val heartRate: Int,
    val timestamp: Long,
    val recordId: Long
){
    companion object {
        const val TABLE_NAME = "heart_rate_polar"
    }
}