package com.example.sensorlogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.sensorlogger.ui.SensorApp
import com.example.sensorlogger.ui.theme.SensorLoggerTheme
import io.appwrite.Client
import io.appwrite.services.Account
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val client = Client(applicationContext)
            .setEndpoint("https://fra.cloud.appwrite.io/v1")
            .setProject("6a21cb6200335638dd99")

        val account = Account(client)

        lifecycleScope.launch {
            try {
                client.ping()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        setContent {
            SensorLoggerTheme {
                SensorApp()
            }
        }
    }
}
