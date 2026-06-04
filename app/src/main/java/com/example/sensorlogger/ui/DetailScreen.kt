package com.example.sensorlogger.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: SensorViewModel,
    sensorType: Int,
    onNavigateBack: () -> Unit
) {
    val availableSensors by viewModel.availableSensors.collectAsState()
    val sensor = availableSensors.find { it.type == sensorType }
    val activeSensorData by viewModel.activeSensorData.collectAsState()
    val currentData = activeSensorData[sensorType]
    val history by viewModel.sensorHistory.collectAsState()
    val sensorHistory = history[sensorType] ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sensor?.name ?: "Sensor Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Live Graph",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Graph View
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (sensorHistory.isNotEmpty()) {
                    SensorGraph(history = sensorHistory, modifier = Modifier.fillMaxSize().padding(16.dp))
                } else {
                    Text(
                        text = "No data available",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Current Readings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            currentData?.values?.forEachIndexed { index, value ->
                val axisName = when (index) {
                    0 -> "X Axis / Primary"
                    1 -> "Y Axis / Secondary"
                    2 -> "Z Axis / Tertiary"
                    else -> "Value ${index + 1}"
                }
                Text(
                    text = "$axisName: $value",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun SensorGraph(history: List<FloatArray>, modifier: Modifier = Modifier) {
    if (history.isEmpty()) return
    val numValues = history.first().size
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val maxVal = history.maxOfOrNull { it.maxOrNull() ?: 0f } ?: 1f
        val minVal = history.minOfOrNull { it.minOrNull() ?: 0f } ?: -1f
        var range = maxVal - minVal
        if (range == 0f) range = 1f

        val stepX = width / (history.size.coerceAtLeast(2) - 1).toFloat()

        for (vIndex in 0 until numValues) {
            val path = Path()
            history.forEachIndexed { hIndex, values ->
                val x = hIndex * stepX
                val value = values.getOrNull(vIndex) ?: 0f
                val y = height - ((value - minVal) / range) * height

                if (hIndex == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            drawPath(
                path = path,
                color = colors[vIndex % colors.size],
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}
