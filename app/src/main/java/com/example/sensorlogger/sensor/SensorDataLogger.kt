package com.example.sensorlogger.sensor

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SensorDataLogger(private val context: Context) {
    private var fileWriter: FileWriter? = null
    private var logFile: File? = null
    private var isLogging = false
    private val scope = CoroutineScope(Dispatchers.IO)

    fun startLogging() {
        if (isLogging) return
        isLogging = true
        scope.launch {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            logFile = File(context.cacheDir, "sensor_log_.csv")
            try {
                fileWriter = FileWriter(logFile)
                fileWriter?.append("Timestamp,SensorName,SensorType,ValueX,ValueY,ValueZ\n")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun logData(data: SensorData) {
        if (!isLogging) return
        scope.launch {
            val x = data.values.getOrNull(0) ?: 0f
            val y = data.values.getOrNull(1) ?: 0f
            val z = data.values.getOrNull(2) ?: 0f
            try {
                fileWriter?.append(",\"\",,,,\n")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun stopLoggingAndExport(): Intent? = withContext(Dispatchers.IO) {
        if (!isLogging) return@withContext null
        isLogging = false
        try {
            fileWriter?.flush()
            fileWriter?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fileWriter = null

        val file = logFile ?: return@withContext null

        val uri: Uri = FileProvider.getUriForFile(
            context,
            ".provider",
            file
        )

        Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Sensor Log Data")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
