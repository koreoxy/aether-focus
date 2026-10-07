package com.aetherfocus.core.designsystem.components

import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherfocus.core.designsystem.theme.CrtBlack
import com.aetherfocus.core.designsystem.theme.CrtBlackAlt
import com.aetherfocus.core.designsystem.theme.CrtRed
import com.aetherfocus.core.designsystem.theme.CrtWhite
import com.aetherfocus.core.designsystem.theme.CyberCyan
import com.aetherfocus.core.designsystem.theme.PhosphorGreen
import com.aetherfocus.core.designsystem.theme.TerminalBorder
import com.aetherfocus.core.designsystem.theme.TerminalBorderDim
import com.aetherfocus.core.designsystem.theme.TextMuted
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated Retro Video Feed container.
 * Plays local MP4 if res/raw/guardian_alert.mp4 exists,
 * otherwise renders an animated retro radar & oscilloscope terminal video stream.
 */
@Composable
fun RetroVideoFeed(
    targetAppName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Check if raw video resource exists in res/raw/guardian_alert
    val rawResId = remember {
        context.resources.getIdentifier("guardian_alert", "raw", context.packageName)
    }

    val videoUri = remember(rawResId) {
        if (rawResId != 0) {
            Uri.parse("android.resource://${context.packageName}/$rawResId")
        } else null
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .border(1.dp, TerminalBorder, RoundedCornerShape(0.dp))
            .background(CrtBlackAlt),
        contentAlignment = Alignment.Center
    ) {
        if (videoUri != null) {
            // Physical MP4 playback
            CrtVideoPlayer(
                videoUri = videoUri,
                modifier = Modifier.fillMaxSize(),
                isMuted = true
            )
        } else {
            // Live Cyberpunk Animated Radar & Oscilloscope Simulation
            AnimatedRadarVideo(targetAppName = targetAppName)
        }

        // CRT Scanline Overlay
        CRTScanlines(scanlineAlpha = 0.12f)
    }
}

@Composable
private fun AnimatedRadarVideo(targetAppName: String) {
    val transition = rememberInfiniteTransition(label = "radar")

    // Radar 360 degree rotation
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_angle"
    )

    // Pulse for target beacon
    val pulse by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CrtBlack),
        contentAlignment = Alignment.Center
    ) {
        // Animated Canvas Radar
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.height / 2f) * 0.85f

            // Radar concentric rings
            drawCircle(
                color = TerminalBorderDim,
                radius = radius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = TerminalBorderDim,
                radius = radius * 0.6f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = TerminalBorderDim,
                radius = radius * 0.3f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // Radar crosshairs
            drawLine(
                color = TerminalBorderDim,
                start = Offset(center.x - radius, center.y),
                end = Offset(center.x + radius, center.y),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = TerminalBorderDim,
                start = Offset(center.x, center.y - radius),
                end = Offset(center.x, center.y + radius),
                strokeWidth = 1.dp.toPx()
            )

            // Rotating sweep line
            val rad = Math.toRadians(angle.toDouble())
            val sweepEnd = Offset(
                (center.x + radius * cos(rad)).toFloat(),
                (center.y + radius * sin(rad)).toFloat()
            )
            drawLine(
                color = PhosphorGreen.copy(alpha = 0.85f),
                start = center,
                end = sweepEnd,
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Intruder blip on the radar
            val blipOffset = Offset(center.x + radius * 0.5f, center.y - radius * 0.3f)
            drawCircle(
                color = CrtRed.copy(alpha = pulse),
                radius = 5.dp.toPx(),
                center = blipOffset
            )
        }

        // Live Telemetry Text Overlay
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .padding(8.dp)
        ) {
            Text(
                text = "● LIVE VIDEO FEED : GUARDIAN CAM 01",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = CrtRed,
                fontSize = 9.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "> INTRUDER DETECTED: ${targetAppName.uppercase()}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = CrtWhite,
                fontSize = 11.sp
            )
            Text(
                text = "> RE-ENGAGING FOCUS PROTOCOL",
                style = MaterialTheme.typography.labelSmall,
                color = PhosphorGreen,
                fontSize = 9.sp
            )
        }
    }
}

