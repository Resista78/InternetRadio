package com.armanmaurya.internetradio.ui.mobile.screens.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PlayingVisualizer(
    modifier: Modifier = Modifier,
    isPlaybackActive: Boolean = true,
    barColor: Color = MaterialTheme.colorScheme.primary,
    barWidth: Dp = 4.dp,
    maxBarHeight: Dp = 24.dp,
    minBarHeight: Dp = 4.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer_transition")

    @Composable
    fun AnimatedBar(duration: Int, targetHeightFactor: Float) {
        val height by infiniteTransition.animateFloat(
            initialValue = minBarHeight.value,
            targetValue = if (isPlaybackActive) maxBarHeight.value * targetHeightFactor else minBarHeight.value,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$duration"
        )
        Box(
            modifier = Modifier
                .width(barWidth)
                .height(height.dp)
                .clip(RoundedCornerShape(100))
                .background(barColor)
        )
    }

    Row(
        modifier = modifier.height(maxBarHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        AnimatedBar(duration = 400, targetHeightFactor = 0.8f)
        AnimatedBar(duration = 500, targetHeightFactor = 1.0f)
        AnimatedBar(duration = 300, targetHeightFactor = 0.6f)
        AnimatedBar(duration = 600, targetHeightFactor = 0.9f)
    }
}
