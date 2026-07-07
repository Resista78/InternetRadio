package com.armanmaurya.internetradio.ui.mobile.screens.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun ScrollingWaveform(
    amplitude: Float,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .height(48.dp)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val barWidth = 3.dp
        val spacing = 3.dp
        val barWithSpacing = barWidth + spacing
        
        // Calculate exactly how many bars can fit in the available width
        val maxBars = (maxWidth / barWithSpacing).toInt()
        
        val amplitudes = remember { mutableStateListOf<Float>() }

        // Update the list when a new amplitude comes in
        LaunchedEffect(amplitude) {
            amplitudes.add(amplitude)
            if (amplitudes.size > maxBars) {
                amplitudes.removeAt(0)
            }
        }

        val barColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
        val emptyColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
        
        val density = LocalDensity.current
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barWidthPx = with(density) { barWidth.toPx() }
            val spacingPx = with(density) { spacing.toPx() }
            val emptyBars = maxBars - amplitudes.size
            
            val minHeightPx = with(density) { 2.dp.toPx() }
            val maxHeightPx = with(density) { 40.dp.toPx() }
            val cornerRadius = CornerRadius(barWidthPx / 2f)
            
            // Draw empty baseline bars
            for (i in 0 until emptyBars) {
                val x = i * (barWidthPx + spacingPx)
                drawRoundRect(
                    color = emptyColor,
                    topLeft = Offset(x, size.height / 2f - minHeightPx / 2f),
                    size = Size(barWidthPx, minHeightPx),
                    cornerRadius = cornerRadius
                )
            }
            
            // Draw actual amplitudes
            val startX = emptyBars * (barWidthPx + spacingPx)
            val centerY = size.height / 2f
            
            amplitudes.forEachIndexed { index, amp ->
                val x = startX + index * (barWidthPx + spacingPx)
                val barHeight = (minHeightPx + (amp * maxHeightPx * 2.5f)).coerceIn(minHeightPx, maxHeightPx)
                
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, centerY - barHeight / 2f),
                    size = Size(barWidthPx, barHeight),
                    cornerRadius = cornerRadius
                )
            }
        }
    }
}
