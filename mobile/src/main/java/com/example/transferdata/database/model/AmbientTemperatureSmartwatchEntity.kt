package com.example.transferdata.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = AmbientTemperatureSmartwatchEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = RecordEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("recordId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["recordId"])]
)
data class AmbientTemperatureSmartwatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val temperature: Float,
    val timestamp: Long,
    val recordId: Long
){
    companion object {
        const val TABLE_NAME = "ambient_temperature_smartwatch"
    }
}