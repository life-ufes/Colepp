package com.example.commons

class CommunicationPaths {
    companion object {
        const val START_ACTIVITY_PATH = "/start-activity"

        const val PING_PATH = "/ping"
        const val PONG_PATH = "/pong"

        const val INIT_TRANSFER_DATA_PATH = "/init-transfer-data"
        const val STOP_TRANSFER_DATA_PATH = "/stop-transfer-data"

        const val ACCELEROMETER_DATA_PATH = "/accelerometer-data"
        const val LINEAR_ACCELERATION_DATA_PATH = "/linear-acceleration-data"
        const val HEART_RATE_DATA_PATH = "/heart-rate-data"
        const val GYROSCOPE_DATA_PATH = "/gyroscope-data"
        const val GRAVITY_DATA_PATH = "/gravity-data"
        const val AMBIENT_TEMPERATURE_DATA_PATH = "/ambient-temperature-data"
    }
}