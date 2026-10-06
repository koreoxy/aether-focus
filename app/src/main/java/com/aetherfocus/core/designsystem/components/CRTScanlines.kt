package com.aetherfocus.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Subtle CRT Scanline overlay giving the authentic 1980s/1990s computer monitor appearance.
 */
@Composable
fun CRTScanlines(
    modifier: Modifier = Modifier,
    scanlineAlpha: Float = 0.08f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val step = 4.dp.toPx()
        val lineCount = (size.height / step).toInt()
        val lineColor = Color.Black.copy(alpha = scanlineAlpha)

        for (i in 0 until lineCount) {
            val y = i * step
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // CRT subtle corner vignette
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF000000).copy(alpha = 0.35f)
                ),
                center = Offset(size.width / 2f, size.height / 2f),
                radius = size.width.coerceAtLeast(size.height) * 0.8f
            )
        )
    }
}
