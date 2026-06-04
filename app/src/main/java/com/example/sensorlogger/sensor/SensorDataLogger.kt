package com.example.sensorlogger.sensor

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SensorDataLogger(private val context: Context) {
    private var fileWriter: FileWriter? = null
    private var logFile: File? = null
    private var isLogging = false

    fun startLogging() {
        if (isLogging) return
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        logFile = File(context.cacheDir, "sensor_log_$timeStamp.csv")
        fileWriter = FileWriter(logFile)
        fileWriter?.append("Timestamp,SensorName,SensorType,ValueX,ValueY,ValueZ\n")
        isLogging = true
    }

    fun logData(data: SensorData) {
        if (!isLogging) return
        val x = data.values.getOrNull(0) ?: 0f
        val y = data.values.getOrNull(1) ?: 0f
        val z = data.values.getOrNull(2) ?: 0f
        
        fileWriter?.append("${data.timestamp},\"${data.sensorName}\",${data.sensorType},$x,$y,$z\n")
    }

    fun stopLoggingAndExport(): Intent? {
        if (!isLogging) return null
        isLogging = false
        fileWriter?.flush()
        fileWriter?.close()
        fileWriter = null

        val file = logFile ?: return null
        
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        return Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Sensor Log Data")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
