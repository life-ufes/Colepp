package com.example.colepp.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = RecordEntity.TABLE_NAME,
    indices = [Index(value = ["id"], unique = true)]
)
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val shared: Int = 0,
    val starRecordingNanos: Long = 0L,
    val starRecordingMilli: Long = 0L,
    val clockSkewSmartwatchNanos: Long = 0L,
    val stopRecordingNanos: Long = 0L,
    val stopRecordingMilli: Long = 0L,
) {
    companion object {
        const val TABLE_NAME = "record"
    }
}