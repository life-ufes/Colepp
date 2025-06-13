package com.example.transferdata.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = RecordEntity.TABLE_NAME,
    indices = [Index(value = ["id"], unique = true)])
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val starRecordingNanos: Long = 0L,
    val starRecordingMilli: Long = 0L,
    val clockSkewSmartwatchNanos: Long = 0L
){
    companion object {
        const val TABLE_NAME = "record"
    }
}