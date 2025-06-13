package com.example.transferdata.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = GyroscopeSmartwatchEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = RecordEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("recordId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["recordId"])]
)
data class GyroscopeSmartwatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long,
    val recordId: Long
){
    companion object {
        const val TABLE_NAME = "gyroscope_smartwatch"
    }
}