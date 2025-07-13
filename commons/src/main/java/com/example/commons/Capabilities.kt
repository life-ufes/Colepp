package com.example.commons

class Capabilities {
    companion object {
        const val WEAR_CAPABILITY = "wear"
        const val ACCELEROMETER_CAPABILITY = "accelerometer-data-transfer"
        const val LINEAR_ACCELERATION_CAPABILITY = "linear-acceleration-data-transfer"
        const val HEART_RATE_CAPABILITY = "heart-rate-data-transfer"
        const val GYROSCOPE_CAPABILITY = "gyroscope-data-transfer"
        const val AMBIENT_TEMPERATURE_CAPABILITY = "ambient-temperature-data-transfer"
        const val GRAVITY_CAPABILITY = "gravity-data-transfer"

        fun <T> getNodeCapabilities(capabilities: Map<String, Set<T>>): Set<T> {
            return with(capabilities) {
                (getOrDefault(ACCELEROMETER_CAPABILITY, emptySet())
                        union getOrDefault(LINEAR_ACCELERATION_CAPABILITY, emptySet())
                        union getOrDefault(HEART_RATE_CAPABILITY, emptySet())
                        union getOrDefault(AMBIENT_TEMPERATURE_CAPABILITY, emptySet())
                        union getOrDefault(GYROSCOPE_CAPABILITY, emptySet())
                        union getOrDefault(GRAVITY_CAPABILITY, emptySet())
                        )
            }
        }
    }
}