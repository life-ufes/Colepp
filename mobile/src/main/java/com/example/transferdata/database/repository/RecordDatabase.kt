package com.example.transferdata.database.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.transferdata.database.model.AccelerometerPolarEntity
import com.example.transferdata.database.model.AccelerometerSmartwatchEntity
import com.example.transferdata.database.model.AmbientTemperatureSmartwatchEntity
import com.example.transferdata.database.model.GyroscopeSmartwatchEntity
import com.example.transferdata.database.model.HeartRatePolarEntity
import com.example.transferdata.database.model.HeartRateSmartwatchEntity
import com.example.transferdata.database.model.RecordEntity
import com.example.transferdata.database.repository.dao.AccelerometerPolarDao
import com.example.transferdata.database.repository.dao.AccelerometerSmartwatchDao
import com.example.transferdata.database.repository.dao.AmbientTemperatureSmartwatchDao
import com.example.transferdata.database.repository.dao.GyroscopeSmartwatchDao
import com.example.transferdata.database.repository.dao.HeartRatePolarDao
import com.example.transferdata.database.repository.dao.HeartRateSmartwatchDao
import com.example.transferdata.database.repository.dao.RecordDAO

@Database(
    entities = [
        RecordEntity::class,
        HeartRatePolarEntity::class,
        AccelerometerPolarEntity::class,
        LinearAccelerationSmartwatchEntity::class,
        AccelerometerSmartwatchEntity::class,
        GyroscopeSmartwatchEntity::class,
        HeartRateSmartwatchEntity::class,
        GravitySmartwatchEntity::class,
        AmbientTemperatureSmartwatchEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RecordDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDAO
    abstract fun heartRatePolarDao(): HeartRatePolarDao
    abstract fun accelerometerPolarDao(): AccelerometerPolarDao
    abstract fun linearAccelerationSmartwatchDao(): LinearAccelerationSmartwatchDao
    abstract fun accelerometerSmartwatchDao(): AccelerometerSmartwatchDao
    abstract fun gyroscopeSmartwatchDao(): GyroscopeSmartwatchDao
    abstract fun heartRateSmartwatchDao(): HeartRateSmartwatchDao
    abstract fun gravitySmartwatchDao(): GravitySmartwatchDao
    abstract fun ambientTemperatureSmartwatchDao(): AmbientTemperatureSmartwatchDao

    companion object {
        private const val DATABASE_NAME = "record_database"
        private lateinit var INSTANCE: RecordDatabase

        fun getInstance(context: Context): RecordDatabase {
            if (!::INSTANCE.isInitialized) {
                synchronized(RecordDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context = context,
                        klass = RecordDatabase::class.java,
                        name = DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration(true) // TODO - isso é apenas para desenvolvimento, remover depois
                        .build()
                }
            }
            return INSTANCE
        }
    }
}