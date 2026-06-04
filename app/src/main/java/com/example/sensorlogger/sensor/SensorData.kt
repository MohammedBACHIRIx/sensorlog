package com.example.sensorlogger.sensor

data class SensorData(
    val sensorType: Int,
    val sensorName: String,
    val values: FloatArray,
    val timestamp: Long
)
