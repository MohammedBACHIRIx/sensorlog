package com.example.sensorlogger.ui

import android.app.Application
import android.content.Intent
import android.hardware.Sensor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sensorlogger.sensor.SensorData
import com.example.sensorlogger.sensor.SensorDataLogger
import com.example.sensorlogger.sensor.SensorManagerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SensorViewModel(application: Application) : AndroidViewModel(application) {
    private val sensorService = SensorManagerService(application)
    private val logger = SensorDataLogger(application)

    private val _availableSensors = MutableStateFlow<List<Sensor>>(emptyList())
    val availableSensors: StateFlow<List<Sensor>> = _availableSensors.asStateFlow()

    private val _activeSensorData = MutableStateFlow<Map<Int, SensorData>>(emptyMap())
    val activeSensorData: StateFlow<Map<Int, SensorData>> = _activeSensorData.asStateFlow()

    private val _sensorHistory = MutableStateFlow<Map<Int, List<FloatArray>>>(emptyMap())
    val sensorHistory: StateFlow<Map<Int, List<FloatArray>>> = _sensorHistory.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val sensorJobs = mutableMapOf<Int, Job>()
    private val MAX_HISTORY_SIZE = 100

    init {
        _availableSensors.value = sensorService.getAvailableSensors()

        _availableSensors.value.forEach { sensor ->
            sensorJobs[sensor.type] = viewModelScope.launch {
                sensorService.observeSensor(sensor).collect { data ->
                    // Update active data
                    _activeSensorData.update { it + (sensor.type to data) }

                    // Update history
                    _sensorHistory.update { currentHistory ->
                        val historyList = currentHistory[sensor.type]?.toMutableList() ?: mutableListOf()
                        historyList.add(data.values.clone())
                        if (historyList.size > MAX_HISTORY_SIZE) {
                            historyList.removeAt(0)
                        }
                        currentHistory + (sensor.type to historyList)
                    }

                    if (_isRecording.value) {
                        logger.logData(data)
                    }
                }
            }
        }
    }

    fun toggleRecording(onExportReady: (Intent) -> Unit) {
        viewModelScope.launch {
            if (_isRecording.value) {
                _isRecording.value = false
                logger.stopLoggingAndExport()?.let { onExportReady(it) }
            } else {
                logger.startLogging()
                _isRecording.value = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorJobs.values.forEach { it.cancel() }
        viewModelScope.launch {
            if (_isRecording.value) {
                logger.stopLoggingAndExport()
            }
        }
    }
}
