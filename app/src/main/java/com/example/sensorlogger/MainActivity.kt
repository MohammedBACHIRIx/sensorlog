package com.example.sensorlogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sensorlogger.ui.SensorApp
import com.example.sensorlogger.ui.theme.SensorLoggerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SensorLoggerTheme {
                SensorApp()
            }
        }
    }
}
